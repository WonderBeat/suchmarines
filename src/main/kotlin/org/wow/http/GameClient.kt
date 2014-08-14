package org.wow.http

import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.apache.http.client.utils.URIBuilder
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.Consts
import org.wow.logger.SerializedMove
import org.wow.logger.GameTurnResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.client.HttpClient
import org.slf4j.LoggerFactory

public class LoginException(): Exception()

public class HttpClientException(): Exception()

trait GameClient {
    fun login(username: String, password: String)
    fun startGame(): String
    fun endGame(gameId: String)
    fun getMovesForPreviousTurn(gameId: String): List<SerializedMove>
}

public class HttpGameClient(val client: HttpClient, val mapper: ObjectMapper, val url: String): GameClient {

    var context = HttpClientContext.create()

    val logger = LoggerFactory.getLogger(javaClass<HttpGameClient>())!!

    override fun login(username: String, password: String) {
        val indexGet = HttpGet(url);
        client.execute(indexGet, context)
        indexGet.releaseConnection()
        var httpPost = HttpPost(url + "/login.html")
        httpPost.setEntity(UrlEncodedFormEntity(listOf(BasicNameValuePair("userName", username),
                BasicNameValuePair("password", password))))
        val httpResponse = client.execute(httpPost, context)
        httpPost.releaseConnection()
        if(httpResponse!!.getStatusLine()!!.getStatusCode() != 302) {
            throw LoginException()
        }
    }

    /**
     * Returns game ID
     */
    override fun startGame(): String {
        var httpPost = HttpPost(url + "/training.html")
        httpPost.setEntity(UrlEncodedFormEntity(listOf(BasicNameValuePair("botsCount", "1"),
                BasicNameValuePair("type", "3")), Consts.UTF_8))
        val httpResponse = client.execute(httpPost, context)
        httpPost.releaseConnection()
        if(httpResponse!!.getStatusLine()!!.getStatusCode() != 302) {
            throw HttpClientException()
        }
        val training = HttpGet(url + "/training.html");
        val traningPage = EntityUtils.toString(client.execute(training, context)!!.getEntity())!!
        training.releaseConnection()
        val gameIdRegexp = ".*var gameId = (\\d{5,30})<".toRegex()
        val matcher = gameIdRegexp.matcher(traningPage)
        if(!matcher.find()) {
            logger.error(traningPage)
        }
        return matcher.group(1)!!
    }

    override fun endGame(gameId: String) {
        val stopGame = HttpGet(URIBuilder(url + "/training.html").setParameter("gameId", gameId)!!.build()!!);
        client.execute(stopGame, context)
        stopGame.releaseConnection()
    }

    override fun getMovesForPreviousTurn(gameId: String): List<SerializedMove> {
        val statusRequest =
                HttpGet(URIBuilder(url + "/game/viewData.html")
                        .setParameter("gameId", gameId)!!
                        .setParameter("type", "PLAYERS_ACTIONS")!!
                        .build()!!);
        val statusJson = EntityUtils.toString(client.execute(statusRequest)!!.getEntity())!!
        statusRequest.releaseConnection()
        return mapper.readValue(statusJson, javaClass<GameTurnResponse>())!!.playersActions.actions
    }


}
