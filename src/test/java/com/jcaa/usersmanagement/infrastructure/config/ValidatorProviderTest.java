package com.jcaa.usersmanagement.infrastructure.config;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests para ValidatorProvider.
 *
 * <p>Verifica que el proveedor de validación retorne una instancia funcional de {@link Validator},
 * capaz de detectar violaciones de restricciones Bean Validation en objetos inválidos y permitir el
 * paso de objetos válidos.
 */
@DisplayName("ValidatorProvider")
class ValidatorProviderTest {

  /**
   * Bean mínimo con una restricción {@code @NotNull} para ejercitar el validador sin acoplarse a
   * clases de producción.
   */
  private record ConstrainedBean(@NotNull String requiredField) {}

  @Test
  @DisplayName("Debe retornar una instancia de validador no nula")
  void shouldReturnNonNullValidator() {
    // Act
    final Validator validator = ValidatorProvider.buildValidator();

    // Assert
    assertNotNull(validator);
  }

  @Test
  @DisplayName("Debe detectar violaciones de restricción en un objeto con campos inválidos")
  void shouldDetectViolationsOnInvalidBean() {
    // Arrange
    final Validator validator = ValidatorProvider.buildValidator();
    final ConstrainedBean invalidBean = new ConstrainedBean(null);

    // Act
    final Set<ConstraintViolation<ConstrainedBean>> violations = validator.validate(invalidBean);

    // Assert
    assertFalse(violations.isEmpty());
  }

  @Test
  @DisplayName("Debe confirmar que no existen violaciones en un objeto con campos válidos")
  void shouldFindNoViolationsOnValidBean() {
    // Arrange
    final Validator validator = ValidatorProvider.buildValidator();
    final ConstrainedBean validBean = new ConstrainedBean("valid value");

    // Act
    final Set<ConstraintViolation<ConstrainedBean>> violations = validator.validate(validBean);

    // Assert
    assertTrue(violations.isEmpty());
  }
}
