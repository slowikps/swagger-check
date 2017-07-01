package de.leanovate.swaggercheck.schema.gen

import de.leanovate.swaggercheck.schema.model.{BooleanDefinition, IntegerDefinition, ObjectDefinition, StringDefinition}
import org.scalacheck.Properties

object GeneratableObjectSpecification extends Properties("GeneratableObject") with DefinitionChecks {
  property("any generates are valid") = {
    val definition = ObjectDefinition(None, None, Left(true), false)

    checkDefinition(definition)
  }

  property("generates with properties are valid") = {
    val definition = ObjectDefinition(Some(Set("field1")), Some(Map(
      "field1" -> StringDefinition(None, None, None, None, None, false),
      "field2" -> IntegerDefinition(None, None, None, false),
      "field3" -> BooleanDefinition(false)
    )), Left(true), false)

    checkDefinition(definition)
  }

  property("generate with additional properties are valid") = {
    val definition = ObjectDefinition(Some(Set("field1")), Some(Map(
      "field1" -> StringDefinition(None, None, None, None, None, false),
      "field2" -> IntegerDefinition(None, None, None, false),
      "field3" -> BooleanDefinition(false)
    )), Right(StringDefinition(None, None, None, None, None, false)), false)

    checkDefinition(definition)
  }
}
