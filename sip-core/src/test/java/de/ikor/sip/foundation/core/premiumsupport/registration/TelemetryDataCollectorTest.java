package de.ikor.sip.foundation.core.premiumsupport.registration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TelemetryDataCollectorTest {
//
//  private final UUID VALID_INSTANCE_ID = UUID.randomUUID();
//  private final String VALID_ADAPTER_NAME = "adapter-name";
//  private final String VALID_INSTANCE_URI = "http://127.0.0.1:8080";
//  private final Long VALID_REGISTER_INTERVAL = 30000L;
//  @Mock
//  RegistrationConfigurationProperties properties;
//  @Mock private Environment environment;
//  @Mock private HealthEndpoint healthEndpoint;
//  @Mock private AdapterRouteEndpoint adapterRouteEndpoint;
//  @Mock private PathMappedEndpoints pathMappedEndpoints;
//  @Mock private HealthComponent healthComponent;
//  @Mock private ManagedRouteMBean managedRouteMBean;
//  private TelemetryDataCollector telemetryDataCollector;
//
//  @BeforeEach
//  void setUp() {
//    MockitoAnnotations.initMocks(this);
//
//    Mockito.when(healthEndpoint.health()).thenReturn(healthComponent);
//    Mockito.when(healthEndpoint.health().getStatus()).thenReturn(Status.UP);
//
//    List<AdapterRouteSummary> list = new ArrayList<>();
//    list.add(new AdapterRouteSummary(managedRouteMBean));
//    Mockito.when(adapterRouteEndpoint.routes()).thenReturn(list);
//
//  }
//
//  @Test
//  void test_If_Telemetry_Adapter_Can_Be_Initialized_With_Valid_Properties() {
//    this.telemetryDataCollector =
//        new TelemetryDataCollector(properties);
//    assertDoesNotThrow(
//        () ->
//            telemetryDataCollector.initialize());
//  }
//
//  @Test
//  void test_If_Telemetry_Adapter_Can_Not_Be_Initialized_With_Null_InstanceId() {
//    assertThrows(
//        IllegalArgumentException.class,
//        () ->
//            telemetryDataCollector.initialize(
//                null, VALID_ADAPTER_NAME, VALID_INSTANCE_URI, VALID_REGISTER_INTERVAL));
//  }
//
//  @ParameterizedTest
//  @ValueSource(
//      strings = {
//        "",
//        "    ",
//        "this-adapter-name-is-way-too-long-you-should-make-sure-that-the-name-does-not-exceed-64-characters",
//      })
//  void test_If_Telemetry_Adapter_Can_Not_Be_Initialized_With_Invalid_Adapter_Name(
//      String adapterName) {
//    assertThrows(
//        IllegalArgumentException.class,
//        () ->
//            telemetryDataCollector.initialize(
//                VALID_INSTANCE_ID, adapterName, VALID_INSTANCE_URI, VALID_REGISTER_INTERVAL));
//  }
//
//  @Test
//  void test_If_Telemetry_Adapter_Can_Not_Be_Initialized_With_Invalid_Instance_Uri() {
//    assertThrows(
//        IllegalArgumentException.class,
//        () ->
//            telemetryDataCollector.initialize(
//                VALID_INSTANCE_ID,
//                VALID_ADAPTER_NAME,
//                "http:\\127.0.0.1:8080",
//                VALID_REGISTER_INTERVAL));
//  }
//
//  @Test
//  void test_If_Telemetry_Adapter_Can_Not_Be_Initialized_With_Invalid_Register_Interval() {
//    assertThrows(
//        IllegalArgumentException.class,
//        () ->
//            telemetryDataCollector.initialize(
//                VALID_INSTANCE_ID, VALID_ADAPTER_NAME, VALID_INSTANCE_URI, null));
//  }
//
//  @Test
//  public void test_If_Collected_Data_Is_Correct() {
//    Mockito.when(properties.getStage()).thenReturn("test");
//
//    telemetryDataCollector.initialize(
//        VALID_INSTANCE_ID, VALID_ADAPTER_NAME, "https://127.0.0.2:8081", 15000L);
//    TelemetryData telemetryData = telemetryDataCollector.collectData();
//    assertThat(telemetryData.getAdapterName()).isEqualTo("adapter-name");
//    assertThat(telemetryData.getInstanceUri()).isEqualTo("https://127.0.0.2:8081");
//    assertThat(telemetryData.getInstanceScheme()).isEqualTo("https");
//    assertThat(telemetryData.getInstanceHost()).isEqualTo("127.0.0.2");
//    assertThat(telemetryData.getInstancePort()).isEqualTo(8081);
//    assertThat(telemetryData.getRegisterInterval()).isEqualTo(15000L);
//    assertThat(telemetryData.getAdapterRoutes().size()).isGreaterThan(0);
//    assertThat(telemetryData.getActiveProfiles().size()).isOne();
//    assertThat(telemetryData.getHealthStatus()).isEqualTo(Status.UP);
//    assertThat(telemetryData.getVersion()).isEqualTo("1.0.0");
//    assertThat(telemetryData.getActuatorEndpoints().size()).isZero();
//  }
//
//  @Test
//  public void test_If_Collected_Data_Is_Correct_By_Default() {
//    Mockito.when(environment.getActiveProfiles()).thenReturn(new String[] {"dev", "additional"});
//    Mockito.when(environment.getProperty("server.ssl.enabled", Boolean.class, false))
//        .thenReturn(true);
//    Mockito.when(environment.getProperty("server.port", Integer.class, 8080)).thenReturn(8080);
//
//    telemetryDataCollector.initialize(VALID_INSTANCE_ID, VALID_ADAPTER_NAME, null, 30000L);
//    TelemetryData telemetryData = telemetryDataCollector.collectData();
//    assertThat(telemetryData.getAdapterName()).isEqualTo("adapter-name");
//    assertThat(telemetryData.getInstanceScheme()).isEqualTo("https");
//    assertThat(telemetryData.getInstancePort()).isEqualTo(8080);
//    assertThat(telemetryData.getRegisterInterval()).isEqualTo(30000L);
//    assertThat(telemetryData.getAdapterRoutes().size()).isGreaterThan(0);
//    assertThat(telemetryData.getActiveProfiles().size()).isEqualTo(2);
//    assertThat(telemetryData.getHealthStatus()).isEqualTo(Status.UP);
//    assertThat(telemetryData.getVersion()).isEqualTo("1.0.0");
//    assertThat(telemetryData.getActuatorEndpoints().size()).isZero();
//  }
}
