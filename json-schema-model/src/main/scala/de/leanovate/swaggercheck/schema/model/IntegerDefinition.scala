package de.leanovate.swaggercheck.schema.model

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter


case class IntegerDefinition(
                              format: Option[String],
                              minimum: Option[BigInt],
                              maximum: Option[BigInt],
                              readOnly: Boolean
                            ) extends Definition {
  override def validate[T](schema: Schema, path: JsonPath, node: T)
                          (implicit nodeAdapter: NodeAdapter[T]): ValidationResult = {
    nodeAdapter.asInteger(node) match {
      case Some(value) =>
        if (minimum.exists(_ > value))
          ValidationResult.error(s"'$value' has to be greater than ${minimum.mkString} in path $path")
        else if (maximum.exists(_ < value))
          ValidationResult.error(s"'$value' has to be less than ${maximum.mkString} in path $path")
        else
          format.flatMap(schema.findIntegerFormat).map(_.validate(path, value)).getOrElse(ValidationResult.success)
      case _ =>
        ValidationResult.error(s"$node should be an integer in path $path")

    }
  }
}