package utils

import java.security.KeyPairGenerator
import java.security.interfaces.{RSAPrivateKey, RSAPublicKey}

import com.nimbusds.jose._
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.util.Base64URL
import models._
import org.jose4j.jwt.JwtClaims
import play.api.Play

import scala.collection.mutable.Map

object OpenIDConnectUtil {

  val clients = Map[String,ClientRegistration]()
  val users = Map[String, User]()
  val gen = KeyPairGenerator.getInstance("RSA")
  gen.initialize(1024)
  val pair = gen.genKeyPair
  val pub = pair.getPublic.asInstanceOf[RSAPublicKey]
  val pri = pair.getPrivate.asInstanceOf[RSAPrivateKey]
  val signer = new RSASSASigner(pri)

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

  private def createClaims(user : User) = {
    val claims = new JwtClaims
    claims.setIssuer("https://morning-chamber-29407.herokuapp.com")
    claims.setAudience(user.client_id)
    claims.setExpirationTimeMinutesInTheFuture(240)
    claims.setGeneratedJwtId()
    claims.setNotBeforeMinutesInThePast(0)
    claims.setIssuedAtToNow()
    claims.setSubject(user.username)
    claims
  }

  def createIDToken(user : User) = {
    val claims = createClaims(user)
    val jws = new JWSObject(new JWSHeader(JWSAlgorithm.RS512),new Payload(claims.toJson))
    jws.sign(signer)
    jws.serialize
  }

  def constructKey() = {
    Keys(Array(PublicKey(n = Base64URL.encode(pub.getModulus).toString)))
  }
}
