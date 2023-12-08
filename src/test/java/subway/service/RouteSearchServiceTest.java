package subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static subway.exception.ErrorMessage.SAME_DEPARTURE_AND_ARRIVAL_STATION;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import subway.controller.constants.RouteSearchCriteria;
import subway.domain.station.Station;
import subway.dto.RouteSearchResultDto;
import subway.init.StationDataLoader;

@DisplayName("경로 기준, 도착역, 출발역을 기준으로 경로를 탐색한다.")
class RouteSearchServiceTest {
    private final RouteSearchService routeSearchService = new RouteSearchService();

    @BeforeAll
    static void beforeAll() {
        StationDataLoader.saveAllStations();
        StationDataLoader.saveAllLines();
    }

    @CsvSource(value = {
            "양재역, 교대역, 3, 4, 11",
            "교대역, 양재역, 3, 4, 11"
    })
    @ParameterizedTest
    void 최단거리기준으로_경로를_올바르게_탐색한다(String departure, String arrival, int size, int distance, int arrivalTime) {
        //given
        //when
        RouteSearchResultDto routeSearchResultDto = routeSearchService.searchRoute(new Station(departure),
                new Station(arrival), RouteSearchCriteria.SHORTEST_DISTANCE);
        //then
        routeSearchResultDto.intermediateStations().forEach(System.out::println);
        assertThat(routeSearchResultDto.intermediateStations()).hasSize(size);
        assertThat(routeSearchResultDto.distance()).isEqualTo(distance);
        assertThat(routeSearchResultDto.arrivalTime()).isEqualTo(arrivalTime);
    }

    @CsvSource(value = {
            "양재역, 교대역, 3, 4, 11",
            "교대역, 양재역, 3, 4, 11"
    })
    @ParameterizedTest
    void 최단시간기준으로_경로를_올바르게_탐색한다(String departure, String arrival, int size, int distance, int arrivalTime) {
        //given
        //when
        RouteSearchResultDto routeSearchResultDto = routeSearchService.searchRoute(new Station(departure),
                new Station(arrival), RouteSearchCriteria.MIN_ARRIVAL_TIME);
        //then
        routeSearchResultDto.intermediateStations().forEach(System.out::println);
        assertThat(routeSearchResultDto.intermediateStations()).hasSize(size);
        assertThat(routeSearchResultDto.distance()).isEqualTo(distance);
        assertThat(routeSearchResultDto.arrivalTime()).isEqualTo(arrivalTime);
    }

    @Test
    void 출발역과_도착역이_동일하면_예외가_발생한다() {
        //given
        String departure = "교대역";
        String arrival = "교대역";
        //when then
        assertThatThrownBy(() -> routeSearchService.searchRoute(new Station(departure),
                new Station(arrival), RouteSearchCriteria.SHORTEST_DISTANCE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(SAME_DEPARTURE_AND_ARRIVAL_STATION.getValue());
    }
}