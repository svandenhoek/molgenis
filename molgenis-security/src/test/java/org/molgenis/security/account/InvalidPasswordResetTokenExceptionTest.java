package org.molgenis.security.account;

import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.molgenis.data.security.auth.PasswordResetToken;
import org.molgenis.util.exception.ExceptionMessageTest;

class InvalidPasswordResetTokenExceptionTest extends ExceptionMessageTest {
  @BeforeEach
  void setUp() {
    messageSource.addMolgenisNamespaces("security");
  }

  @ParameterizedTest
  @MethodSource("languageMessageProvider")
  @Override
  protected void testGetLocalizedMessage(String lang, String message) {
    PasswordResetToken passwordResetToken = mock(PasswordResetToken.class);
    assertExceptionMessageEquals(
        new InvalidPasswordResetTokenException(passwordResetToken), lang, message);
  }

  static Object[][] languageMessageProvider() {
    Object[] enParams = {"en", "The password reset link is invalid."};
    Object[] nlParams = {"nl", "De wachtwoord reset link is ongeldig."};
    return new Object[][] {enParams, nlParams};
  }
}
