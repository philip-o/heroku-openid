package utils

import scala.collection.mutable.Map
import models.{ClientRegistration, Configuration, User}
import org.jose4j.jwe.{ContentEncryptionAlgorithmIdentifiers, JsonWebEncryption, KeyManagementAlgorithmIdentifiers}
import org.jose4j.jwk.{JsonWebKey, JsonWebKeySet}
import org.jose4j.jws.{AlgorithmIdentifiers, JsonWebSignature}
import org.jose4j.jwt.JwtClaims
import play.api.Play

object OpenIDConnectUtil {

  val clients = Map[String,ClientRegistration]()
  val users = Map[String, User]()

  def checkEncoding(config : Configuration, request : ClientRegistration) = {
    if(config.id_token_encryption_alg_values_supported.contains(request.id_token_encrypted_response_alg) &&
      config.id_token_encryption_enc_values_supported.contains(request.id_token_encrypted_response_enc)
      && config.id_token_signing_alg_values_supported.contains(request.id_token_signed_response_alg)
      && config.userinfo_encryption_alg_values_supported.contains(request.userinfo_encrypted_response_alg)
      && config.userinfo_encryption_enc_values_supported.contains(request.userinfo_encrypted_response_enc)
      && config.userinfo_signing_alg_values_supported.contains(request.userinfo_signed_response_alg))
    { "valid" }
    else {"encoding_or_algoirthm_unsupported" }
  }

  def loadConfig(property : String) = {
    import play.api.Play.current
    Play.configuration.getStringSeq(property).getOrElse(throw new RuntimeException(s"Unable to find property $property"))
  }

  def createToken(user : User) : String = {
    val claims = createClaims(user)
    val jwk = JsonWebKey.Factory.newJwk("{\"kty\":\"oct\",\"k\":\"Fdh9u8rINxfivbrianbbVT1u232VQBZYKx1HGAGPt2I\"}")
    val jwe = new JsonWebEncryption
    jwe.setPayload(claims.toJson)
    jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.DIRECT)
    jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256)
    jwe.setKey(jwk.getKey)
    jwe.getCompactSerialization
  }

  def createJWS(user : User) = {
    val claims = createClaims(user)
    val jwks = new JsonWebKeySet("{\"keys\":[{\"alg\": \"RS256\",\"d\": \"SFJgqZqf7vpOcLgVjQ6ushnZzdMXj0dmrDKeNg5VKdvAlee-M4CFQH_CjSXrx5cSrAYMIexpi7FDZVLRS-8LSHTqN8uDukrvGTLf3C0ezejYAWGDWvCH17Q7NCPpEeL25Dll3EnPLWQy2nIU_sEz7boEuWnEUL3djqxaoX7yZx5rfHuv-TKdxaGgBpxge4j3OfwkQnNNHLxSydnYTRWJFLy2INKSuIVZG0-AeDMiRfKl0xkYUC8SYhDWP_hF6DNVY8x_9RvQ_-j78Lv-2Ndjr52ck6Ui6FO1k_8qgtid6cBNOsKa1xCiWRrvC89I_ExPa3WA8kREcL_P_n9r66xdAQ\",\"e\": \"AQAB\",\"n\": \"rvnioIqZaydxDwgSzHojZAf5uMAWtDvI15Azy8yxwAvkpYDe1wKAifOhKVxSsFa9pc88aJFFVMe9rkDumVS_DNrT0LmlBqQAV2sklYTd7jq5yJh3HuI83VXqTgQ1ITqaACdo_nwZ7NP__LhSHYtxGHoM4qac56z4GrTvph67jw9NdSKHwDtQFoQid6f9kXXzcmC8T7t957eZbVyJ1eexm1eGmxpq2ira5-02YF-fuqzyAZN8idcyXYq4nnXfbCmoM8JEBtzcZLw3uYaL3cGEd1n0VcbkqiBBGRDLCVlqe_PhOkVtQfuNDIuA-ikhmd4o_OzjCSGPrrnR6y0F6dpLYw\",\"kty\": \"RSA\",\"use\": \"sig\",\"kid\": \"Demo Keys\"}]}")
    val jws = new JsonWebSignature
    jws.setPayload(claims.toJson)
    jws.setKey(jwks.getJsonWebKeys.get(0).getKey)
    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256)
    jws.getCompactSerialization
  }

  private def createClaims(user : User) = {
    val claims = new JwtClaims
    claims.setIssuer("morning-chamber-29407.herokuapp.com")
    claims.setAudience(user.client_id)
    claims.setExpirationTimeMinutesInTheFuture(240)
    claims.setGeneratedJwtId()
    claims.setNotBeforeMinutesInThePast(0)
    claims.setIssuedAtToNow()
    claims.setSubject(user.username)
    claims
  }

  def main(args: Array[String]) = {
    println(OpenIDConnectUtil.createJWS(User("philip","password","state","consumer","client_id")))
  }
}
