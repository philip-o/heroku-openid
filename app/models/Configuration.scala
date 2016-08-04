package models

import play.api.libs.json.Json

case class Configuration(issuer : String,
                         authorization_endpoint :String,
                         token_endpoint : String,
                         userinfo_endpoint : String,
                         jwks_uri : String,
                         registration_endpoint : String,
                         scopes_supported : Seq[String],
                         response_types_supported : Seq[String],
                         subject_types_supported : Seq[String],
                         id_token_signing_alg_values_supported : Seq[String],
                         id_token_encryption_enc_values_supported : Seq[String],
                         id_token_encryption_alg_values_supported : Seq[String],
                         userinfo_signing_alg_values_supported : Seq[String],
                         userinfo_encryption_enc_values_supported : Seq[String],
                         userinfo_encryption_alg_values_supported : Seq[String],
                         claims_supported : Seq[String]
                        )

object Configuration {
  implicit val formats = Json.format[Configuration]
}

case class ClientRequest(redirect_uris : Array[String],
                              client_name: String,
                              jwks_uri : String,
                              id_token_signed_response_alg : String,
                              id_token_encrypted_response_alg : String,
                              id_token_encrypted_response_enc : String,
                              userinfo_signed_response_alg : String,
                              userinfo_encrypted_response_alg : String,
                              userinfo_encrypted_response_enc : String
                             )

object ClientRequest {
  implicit val format = Json.format[ClientRequest]
}

case class ClientRegistration(redirect_uris : Array[String],
                              client_name: String,
                              jwks_uri : String,
                              id_token_signed_response_alg : String,
                              id_token_encrypted_response_alg : String,
                              id_token_encrypted_response_enc : String,
                              userinfo_signed_response_alg : String,
                              userinfo_encrypted_response_alg : String,
                              userinfo_encrypted_response_enc : String,
                              client_id : String = ""
                             )

object ClientRegistration {
  implicit val format = Json.format[ClientRegistration]
}

case class ClientRegistrationResponse(client_id : String,
                                      client_secret : String,
                                      registration_access_token : String,
                                      registration_client_uri : String,
                                      client_id_issued_at : Long,
                                      client_secret_expires_at : Long,
                                      redirect_uris : Array[String],
                                      client_name: String,
                                      jwks_uri : String,
                                      id_token_signed_response_alg : String,
                                      id_token_encrypted_response_alg : String,
                                      id_token_encrypted_response_enc : String,
                                      userinfo_signed_response_alg : String,
                                      userinfo_encrypted_response_alg : String,
                                      userinfo_encrypted_response_enc : String)

object ClientRegistrationResponse {
  implicit val format = Json.format[ClientRegistrationResponse]
}

case class ClientRegRequestError(error : String, error_description : String)

object ClientRegRequestError {
  implicit val formats = Json.format[ClientRegRequestError]
}


