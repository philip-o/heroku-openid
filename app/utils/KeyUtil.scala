package utils

import java.io.{FileInputStream, FileOutputStream}
import java.security.{KeyFactory, KeyPairGenerator}
import java.security.interfaces.{RSAPrivateKey, RSAPublicKey}
import java.security.spec.{PKCS8EncodedKeySpec, RSAPrivateKeySpec, X509EncodedKeySpec}

object KeyUtil {

  def persist() = {
    val gen = KeyPairGenerator.getInstance("RSA")
    gen.initialize(512)
    val pair = gen.genKeyPair
    val pub = pair.getPublic.asInstanceOf[RSAPublicKey]
    val pri = pair.getPrivate.asInstanceOf[RSAPrivateKey]
    val bytes = pub.getEncoded
    var fos = new FileOutputStream("pub.pub")
    fos.write(bytes)
    fos.close
    fos = new FileOutputStream("pri.pri")
    fos.write(bytes)
    fos.close
  }

  def load() = {
    var fis = new FileInputStream("pub.pub")
    var bytes = new Array[Byte](fis.available)
    fis.read(bytes)
    fis.close
    val pub = new X509EncodedKeySpec(bytes)
    val fac = KeyFactory.getInstance("RSA")
    val publicKey = fac.generatePublic(pub).asInstanceOf[RSAPublicKey]
    println(s"${publicKey.getAlgorithm}")
    fis = new FileInputStream("pri.pri")
    bytes = new Array[Byte](fis.available)
    fis.read(bytes)
    fis.close
    val pri = new PKCS8EncodedKeySpec(bytes)
    val privateKey = KeyFactory.getInstance("RSA","BC").generatePrivate(pri).asInstanceOf[RSAPrivateKey]
    println(s"${privateKey.getAlgorithm}")
  }

  def main(args: Array[String]) = {
    KeyUtil.persist
    KeyUtil.load
  }
}
