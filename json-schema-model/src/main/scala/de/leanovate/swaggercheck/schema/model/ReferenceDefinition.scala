package de.leanovate.swaggercheck.schema.model

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter

case class ReferenceDefinition(
                                ref: String,
                                readOnly: Boolean
                              ) extends Definition {
  override def validate[T](schema: Schema, path: JsonPath, node: T)
                          (implicit nodeAdapter: NodeAdapter[T]): ValidationResult = {
    schema.findByRef(ref)
      .map(_.validate(schema, path, node))
      .getOrElse(ValidationResult.error(s"Referenced definition does not exists: $ref"))

  }
}
