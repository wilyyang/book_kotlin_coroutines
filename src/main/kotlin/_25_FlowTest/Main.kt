package _25_FlowTest

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import java.time.Instant

class ApiException(val code : Int) : Exception()

class Appointment(val name : String, val date : Instant)

sealed class AppointmentsEvent
data object AppointmentsConfirmed : AppointmentsEvent()
class AppointmentsUpdate(val appointments: List<Appointment>) : AppointmentsEvent()

interface AppointmentRepository {
    fun observeAppointments() : Flow<AppointmentsEvent>
}

class ObserveAppointmentsService(
    private val appointmentRepository: AppointmentRepository
){
    fun observeAppointments() : Flow<List<Appointment>> =
        appointmentRepository
            .observeAppointments()
            .filterIsInstance<AppointmentsUpdate>()
            .map { it.appointments }
            .distinctUntilChanged()
            .retry {
                it is ApiException && it.code in 500..599
            }
}

class FakeAppointmentRepository(
    private val flow : Flow<AppointmentsEvent>
) : AppointmentRepository {
    override fun observeAppointments() = flow
}

class ObserveAppointmentsServiceTest {
    val aData1 = Instant.parse("2020-08-30T18:43:00Z")
    val anAppointment1 = Appointment("APP1", aData1)
    val aData2 = Instant.parse("2020-08-31T18:43:00Z")
    val anAppointment2 = Appointment("APP2", aData2)

    fun `오직 AppointmentsUpdate 가져오기`() = runTest {
        val repo = FakeAppointmentRepository(
            flowOf(
                AppointmentsConfirmed,
                AppointmentsUpdate(listOf(anAppointment1)),
                AppointmentsUpdate(listOf(anAppointment2)),
                AppointmentsConfirmed
            )
        )

        val service = ObserveAppointmentsService(repo)
        val result = service.observeAppointments().toList()


    }
}