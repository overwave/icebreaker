import "./Maps.css";
import React, { useCallback, useEffect, useRef, useState } from "react";
import {
  YMaps,
  Map,
  Polyline,
  Placemark,
  ObjectManager,
  Clusterer,
} from "@pbe/react-yandex-maps";
import icon from "../../images/icon.png";
import pointIcon from "../../images/point.svg";
import icebreaker1 from "../../images/icebreakers/icebreaker-1.png";
import icebreaker2 from "../../images/icebreakers/icebreaker-2.png";
import icebreaker3 from "../../images/icebreakers/icebreaker-3.png";
import icebreaker4 from "../../images/icebreakers/icebreaker-4.png";

export default function Maps({
  navPoints,
  shipGeo,
  shipRoute,
  iceRoute,
  idIcebreaker,
}) {
  const [geometryPoints, setGeometryPoints] = useState([]);
  const [geometry, setGeometry] = useState([]);
  const [icebreakersInfo, setIcebreakersInfo] = useState([]);
  const [shipsInfo, setShipsInfo] = useState([]);

  const ymap = useRef(null);
  const ymaps = useRef(null);
  const testGeometry = [
    [70.28319, 57.787407],
    [71.284454, 60.68428],
    [74.284454, 62.68428],
    [75.284454, 66.68428],
  ];

  const onLoad = useCallback(
    (it) => {
      ymaps.current = it;
      const layer = new ymaps.current.Layer(
        "https://overwave.dev/icebreaker/tiles/2024-03-03/%z/%x-%y.jpg",
        { tileTransparent: true }
      );
      ymap.current.layers.add(layer);
    },
    [ymap, ymaps]
  );
  const onMapReference = useCallback(
    (ref) => {
      ymap.current = ref;
    },
    [ymap]
  );

  useEffect(() => {
    let newRoute = [];
    let newIcebreakersInfo = [];
    let newShipsInfo = [];

    if (shipRoute.length) {
      shipRoute.map((r) => {
        r.routes.map((route) => {
          const point = [route.point.lat, route.point.lon];
          newRoute.push(point);

          newIcebreakersInfo.push({
            convoy: r.convoy,
            id: r.icebreaker,
          });
        });
      });
    } else if (iceRoute.length) {
      iceRoute.map((r) => {
        r.routes.map((route) => {
          const point = [route.point.lat, route.point.lon];
          newRoute.push(point);
        });

        newShipsInfo.push({
          ships: r.ships,
        });
      });
    }

    setGeometry(newRoute);
    setIcebreakersInfo(newIcebreakersInfo);
    setShipsInfo(newShipsInfo);
  }, []);

  useEffect(() => {
    if (navPoints.length) {
      let arr = [];
      navPoints.map((point) => {
        arr.push({
          point: [point.point.lat, point.point.lon],
          name: point.name,
        });
      });
      setGeometryPoints(arr);
    }
  }, [navPoints]);

  function getIcebreakerIcon(id) {
    const str = String(id);
    if (str === "48") {
      return icebreaker1;
    } else if (str === "49") {
      return icebreaker2;
    } else if (str === "50") {
      return icebreaker3;
    } else if (str === "51") {
      return icebreaker4;
    } else {
      return icebreaker1;
    }
  }

  return (
    <>
      <YMaps
        query={{
          load: "Map,Layer,geoObject.addon.balloon",
          lang: "ru_RU",
          ns: "use-load-option",
          apikey: "c5678c43-0304-4bfb-8d59-84de14c585fe",
        }}
      >
        <Map
          defaultState={{ center: [73.290841, 55.573121], zoom: 5 }}
          options={{
            yandexMapDisablePoiInteractivity: true,
            minZoom: 3,
            maxZoom: 8,
          }}
          onLoad={onLoad}
          instanceRef={onMapReference}
          style={{ width: "100%", height: "100%" }}
        >
          <Polyline
            geometry={geometry}
            options={{
              balloonCloseButton: false,
              strokeColor: "#4480F3",
              strokeWidth: 2,
            }}
          ></Polyline>

          <Clusterer
            options={{
              preset: "islands#invertedBlueClusterIcons",
              groupByCoordinates: false,
            }}
          >
            {geometryPoints.map((point, index) => {
              return (
                <Placemark
                  key={index}
                  defaultGeometry={point.point}
                  options={{
                    iconLayout: "default#image",
                    iconImageSize: [17, 17],
                    iconImageHref: pointIcon,
                    iconOffset: [3, 29],
                  }}
                  properties={{
                    balloonContentBody: point.name,
                  }}
                />
              );
            })}
          </Clusterer>

          {shipRoute.length !== 0 && (
            <Placemark
              geometry={geometry[shipGeo]}
              options={{
                iconLayout: "default#image",
                iconImageSize: [105, 57],
                iconImageHref: icon,
                iconOffset: [-35, 0],
              }}
            />
          )}

          {icebreakersInfo[shipGeo] && icebreakersInfo[shipGeo].convoy && (
            <Placemark
              geometry={geometry[shipGeo]}
              options={{
                iconLayout: "default#image",
                iconImageSize: [126, 68],
                iconImageHref: getIcebreakerIcon(icebreakersInfo[shipGeo].id),
                iconOffset: [35, -35],
              }}
            />
          )}

          {iceRoute.length !== 0 && (
            <Placemark
              geometry={geometry[shipGeo]}
              options={{
                iconLayout: "default#image",
                iconImageSize: [126, 68],
                iconImageHref: getIcebreakerIcon(idIcebreaker),
                iconOffset: [-35, 0],
              }}
            />
          )}

          {shipsInfo[shipGeo] && shipsInfo[shipGeo].ships && (
            <Placemark
              geometry={geometry[shipGeo]}
              options={{
                iconLayout: "default#image",
                iconImageSize: [105, 57],
                iconImageHref: icon,
                iconOffset: [35, -35],
              }}
            />
          )}
        </Map>
      </YMaps>
    </>
  );
}
