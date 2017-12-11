package eu.reactivesystems.league.infra

import java.nio.charset.StandardCharsets

import akka.serialization.Serializer

/**
  * league-akka
  */
class JsonSerializer extends Serializer {
  override def identifier: Int = JsonSerializer.identifier
  private val charset = StandardCharsets.UTF_8

  override def toBinary(o: AnyRef): Array[Byte] =
    JsonRegistry.mappings
      .get(o.getClass.getName)
      .fold(throw new RuntimeException(
        s"No serializer found for ${o.getClass.getName} in JsonRegistry"))(
        writer => writer.write(o).compactPrint.getBytes(charset))

  override def includeManifest: Boolean = false

  override def fromBinary(bytes: Array[Byte],
                          manifest: Option[Class[_]]): AnyRef = ???
}

object JsonSerializer {
  val identifier = 101
}
