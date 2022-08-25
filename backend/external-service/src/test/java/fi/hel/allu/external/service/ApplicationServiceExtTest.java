package fi.hel.allu.external.service;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceExtTest {

		private final Integer applicationId = 1;
		@Mock
		ApplicationServiceComposer applicationServiceComposer;
		@InjectMocks
		private ApplicationServiceExt applicationServiceExt;

		private static Stream<Arguments> provideNotAllowedStates() {
				return Stream.of(Arguments.of(StatusType.OPERATIONAL_CONDITION, "application.ext.notAllowedWhenOpCond"),
												 Arguments.of(StatusType.FINISHED, "application.ext.notAllowedWhenFinished"),
												 Arguments.of(StatusType.CANCELLED, "application.ext.notAllowedWhenCancelled"),
												 Arguments.of(StatusType.ARCHIVED, "application.ext.notAllowedWhenArchived"));
		}

		@Test
		void validateModifications() {
				ApplicationJson applicationJson = new ApplicationJson();
				applicationJson.setApplicationId(applicationId.toString());
				applicationJson.setStatus(StatusType.PENDING_CLIENT);
				when(applicationServiceComposer.findApplicationById(applicationId)).thenReturn(applicationJson);
				applicationServiceExt.validateModificationAllowed(applicationId);
				verify(applicationServiceComposer, times(1)).findApplicationById(anyInt());
		}

		@ParameterizedTest
		@MethodSource("provideNotAllowedStates")
		void validateModificationsError(StatusType statusType, String expectedError) {
				ApplicationJson applicationJson = new ApplicationJson();
				applicationJson.setApplicationId(applicationId.toString());
				applicationJson.setStatus(statusType);
				when(applicationServiceComposer.findApplicationById(applicationId)).thenReturn(applicationJson);

				Assertions.assertThatExceptionOfType(IllegalOperationException.class)
								.isThrownBy(() -> applicationServiceExt.validateModificationAllowed(applicationId))
								.withMessage(expectedError);
		}

}