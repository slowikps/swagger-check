package de.leanovate.swaggercheck.schema.model

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter

/**
  * Element of a json schema model.
  */
trait Definition {
  /**
    * Validate a json node versus this schema object.
    *
    * @param schema the overall model
    * @param path the current json path elements
    * @param node the node to validate
    * @param nodeAdapter adapter for the various json node implementations one might find
    * @return validation result
    */
  def validate[T](schema: Schema, path: JsonPath, node: T)(implicit nodeAdapter: NodeAdapter[T]): ValidationResult
}

object Definition {
  def build(
             schemaType: Option[String],
             allOf: Option[Seq[Definition]],
             enum: Option[List[String]],
             exclusiveMinimum: Option[Boolean],
             exclusiveMaximum: Option[Boolean],
             format: Option[String],
             items: Option[Definition],
             minItems: Option[Int],
             maxItems: Option[Int],
             minimum: Option[BigDecimal],
             maximum: Option[BigDecimal],
             minLength: Option[Int],
             maxLength: Option[Int],
             oneOf: Option[Seq[Definition]],
             pattern: Option[String],
             properties: Option[Map[String, Definition]],
             additionalProperties: Option[Either[Boolean, Definition]],
             required: Option[Set[String]],
             ref: Option[String],
             uniqueItems: Option[Boolean],
             readOnly: Option[Boolean]
           ): Definition = {
    (allOf, oneOf) match {
      case (Some(definitions), _) =>
        AllOfDefinition(definitions)
      case (_, Some(definitions)) =>
        OneOfDefinition(definitions)
      case _ =>
        schemaType match {
          case Some("object") => ObjectDefinition(required, properties, additionalProperties.getOrElse(Left(true)), readOnly.getOrElse(false))
          case Some("array") => ArrayDefinition(minItems, maxItems, items, readOnly.getOrElse(false))
          case Some("string") => StringDefinition(format, minLength, maxLength, pattern, enum, readOnly.getOrElse(false))
          case Some("integer") => IntegerDefinition(format, minimum.map(_.longValue()), maximum.map(_.longValue()), readOnly.getOrElse(false))
          case Some("number") => NumberDefinition(format, minimum.map(_.doubleValue()), maximum.map(_.doubleValue()), readOnly.getOrElse(false))
          case Some("boolean") => BooleanDefinition(readOnly.getOrElse(false))
          case _ if ref.isDefined => ReferenceDefinition(ref.get)
          case _ => EmptyDefinition
        }
    }
  }
}