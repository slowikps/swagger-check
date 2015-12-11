package de.leanovate.swaggercheck.schema.model

import de.leanovate.swaggercheck.schema.adapter.NodeAdapter

case class ObjectDefinition(
                             required: Option[Set[String]],
                             properties: Option[Map[String, Definition]],
                             additionalProperties: Either[Boolean, Definition]
                           ) extends Definition {
  override def validate[T](schema: Schema, path: JsonPath, node: T)
                          (implicit nodeAdapter: NodeAdapter[T]): ValidationResult[T] = {
    nodeAdapter.asObject(node) match {
      case Some(fields) =>
        val propertiesResult = properties.map {
          props =>
            props.foldLeft(ValidationResult.success(node)) {
              case (result, (name, defintion)) =>
                val field = fields.getOrElse(name, nodeAdapter.createNull)
                if (!nodeAdapter.isNull(field) || required.exists(_.contains(name)))
                  result.combine(defintion.validate(schema, path.field(name), field))
                else
                  result
            }
        }.getOrElse(ValidationResult.success(node))
        val additionalPropertiesResult: ValidationResult[T] = additionalProperties match {
          case Left(true) => ValidationResult.success(node)
          case Left(false) => properties match {
            case Some(props) => fields.keySet.find(!props.contains(_))
              .map(name => ValidationResult.error[T](s"Unknown field $name in path $path"))
              .getOrElse(ValidationResult.success(node))
            case None if fields.nonEmpty => ValidationResult.error(s"Object should not have any field in path $path")
            case None => ValidationResult.success(node)
          }
          case Right(definition) =>
            val explicitFields = properties.map(_.keySet).getOrElse(Set.empty)
            fields.foldLeft(ValidationResult.success(node)) {
              case (result, (name, field)) if explicitFields.contains(name) => result
              case (result, (name, field)) =>
                result.combine(definition.validate(schema, path.field(name), field))
            }
        }
        propertiesResult.combine(additionalPropertiesResult)
      case _ =>
        ValidationResult.error(s"$node should be an object in path $path")
    }
  }
}
