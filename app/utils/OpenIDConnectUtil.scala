package utils

import scala.collection.mutable.Map
import models.{ClientRegistration, Configuration}
import play.api.Play

object OpenIDConnectUtil {

  val clients = Map[String,ClientRegistration]()

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
}
