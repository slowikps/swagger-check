package de.leanovate.swaggercheck.schema.model

import org.scalatest.mockito.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}

class BooleanDefinitionSpec extends WordSpec with MockitoSugar with MustMatchers {
  "BooleanDefinition" should {
    "succeed on any boolean value" in {
      val path = JsonPath("jsonpath")
      val node = TestNode(boolean = Some(true))
      val schema = mock[Schema]

      val definition = BooleanDefinition(false)

      definition.validate(schema, path, node)  mustBe ValidationSuccess
    }

    "fail validation on everything that is not a boolean" in {
      val path = JsonPath("jsonpath")
      val node = TestNode()
      val schema = mock[Schema]

      val definition = BooleanDefinition(false)

      val ValidationFailure(result) = definition.validate(schema, path, node)

      result must have size 1
      result.head must endWith("should be a boolean in path jsonpath")
    }
  }
}
