package utils

import scala.collection.mutable.Map
import models.{ClientRegistration, Configuration, User}
import org.jose4j.jwe.{ContentEncryptionAlgorithmIdentifiers, JsonWebEncryption, KeyManagementAlgorithmIdentifiers}
import org.jose4j.jwk.JsonWebKey
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

  def createToken(user : User) = {
    val claims = new JwtClaims
    claims.setIssuer("morning-chamber-29407.herokuapp.com")
    claims.setAudience(user.client_id)
    claims.setExpirationTimeMinutesInTheFuture(240)
    claims.setGeneratedJwtId()
    claims.setNotBeforeMinutesInThePast(0)
    claims.setIssuedAtToNow()
    claims.setSubject(user.username)
    val jwk = JsonWebKey.Factory.newJwk("{\"kty\":\"oct\",\"k\":\"Fdh9u8rINxfivbrianbbVT1u232VQBZYKx1HGAGPt2I\"}")
    val jwe = new JsonWebEncryption
    jwe.setPayload(claims.toJson)
    jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.DIRECT)
    jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256)
    jwe.setKey(jwk.getKey)
    jwe.getCompactSerialization
  }
}
