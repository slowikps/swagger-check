package de.leanovate.swaggercheck.schema.model

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter
import de.leanovate.swaggercheck.schema.model.formats.{IntegerFormats, NumberFormats, StringFormats, ValueFormat}

case class RootSwaggerSchema(
                          root: Definition,
                          definitions: Map[String, Definition],
                          parameters: Map[String, Parameter],
                          stringFormats: Map[String, ValueFormat[String]] = StringFormats.defaultFormats,
                          integerFormats: Map[String, ValueFormat[BigInt]] = IntegerFormats.defaultFormats,
                          numberFormats: Map[String, ValueFormat[BigDecimal]] = NumberFormats.defaultFormats
                        ) extends Schema {

  override def findByRef(ref: String): Option[Definition] = {
    val simpleRef = if (ref.startsWith("#/definitions/")) ref.substring(14) else ref

    definitions.get(simpleRef)
  }

  override def findNumberFormat(name: String): Option[ValueFormat[BigDecimal]] = numberFormats.get(name)

  override def findIntegerFormat(name: String): Option[ValueFormat[BigInt]] = integerFormats.get(name)

  override def findStringFormat(name: String): Option[ValueFormat[String]] = stringFormats.get(name)

  /**
    * Add a self-defined string format.
    */
  def withStringFormats(formats: (String, ValueFormat[String])*): RootSwaggerSchema =
    copy(stringFormats = stringFormats ++ Map(formats: _*))

  /**
    * Add a self-defined integer format.
    */
  def withIntegerFormats(formats: (String, ValueFormat[BigInt])*): RootSwaggerSchema =
    copy(integerFormats = integerFormats ++ Map(formats: _*))

  /**
    * Add a self-defined number format.
    */
  def withNumberFormats(formats: (String, ValueFormat[BigDecimal])*): RootSwaggerSchema =
    copy(numberFormats = numberFormats ++ Map(formats: _*))

  def validate[T](node: T)(implicit nodeAdapter: NodeAdapter[T]): ValidationResult =
    root.validate(this, JsonPath(), node)
}

object RootSwaggerSchema {
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
             definitions: Option[Map[String, Definition]],
             parameters: Option[Map[String, Parameter]]
           ): RootSwaggerSchema = {
    RootSwaggerSchema(
      Definition.build(schemaType, allOf, enum, exclusiveMinimum, exclusiveMaximum, format, items, minItems, maxItems,
        minimum, maximum, minLength, maxLength, oneOf, pattern, properties, additionalProperties, required, ref,
        uniqueItems, Some(false)),
      definitions.getOrElse(Map.empty),
      parameters.getOrElse(Map.empty)
    )
  }
}