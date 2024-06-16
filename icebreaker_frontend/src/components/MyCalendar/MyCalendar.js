import "./MyCalendar.css";
import "react-calendar/dist/Calendar.css";
import { useEffect, useRef, useState } from "react";
import Time from "../Time/Time";

function MyCalendar({ addZero, shipRoute, shipGeo, setShipGeo, iceRoute }) {
  const [linearCalendar, setLinearCalendar] = useState([]);

  // Стрелки
  const [isArrowLeft, setIsArrowLeft] = useState(false);
  const [isArrowRight, setIsArrowRight] = useState(false);
  const [scrollCalendar, setScrollCalendar] = useState(0);

  const calendarContainer = useRef(null);
  const calendarBlock = useRef(null);
  const calendarList = useRef(null);

  let num = undefined;

  useEffect(() => {
    let newCalendar = [];
    setLinearCalendar(newCalendar);
  }, []);

  useEffect(() => {
    if (
      calendarContainer.current.offsetWidth < calendarList.current.offsetWidth
    ) {
      setIsArrowRight(true);
    }

    calendarBlock.current.scrollLeft = 0;
  }, [calendarContainer, calendarList, linearCalendar]);

  function handleTranslateCalendar(i) {
    const block = calendarBlock.current;
    const widthCalendar = calendarContainer.current.offsetWidth;

    let newScroll = i
      ? scrollCalendar - widthCalendar / 2
      : scrollCalendar + widthCalendar / 2;

    newScroll = checkRight(newScroll, block);
    newScroll = checkLeft(newScroll);

    block.scrollLeft = newScroll;
    setScrollCalendar(newScroll);
  }

  function checkLeft(newScroll) {
    if (newScroll <= 0) {
      setIsArrowLeft(false);
      return 0;
    } else {
      setIsArrowLeft(true);
      return newScroll;
    }
  }

  function checkRight(newScroll, block) {
    if (newScroll >= block.scrollLeftMax) {
      setIsArrowRight(false);
      return block.scrollLeftMax;
    } else {
      setIsArrowRight(true);
      return newScroll;
    }
  }

  function clickRight() {
    handleTranslateCalendar(false);
  }

  function clickLeft() {
    handleTranslateCalendar(true);
  }

  return (
    <div className="calendar">
      <div className="calendar-linear" ref={calendarContainer}>
        <div className="calendar-linear__block" ref={calendarBlock}>
          <ul className="calendar-linear__list" ref={calendarList}>
            {shipRoute.map((r) => {
              let visibleDay = "";

              return r.routes.map((route, index) => {
                let isDay = false;

                if (num === undefined) {
                  num = 0;
                } else {
                  num++;
                }

                const date = new Date(route.time * 1000);
                const day = `${addZero(date.getDate())}.${addZero(
                  date.getMonth() + 1
                )}`;
                const time = `${addZero(date.getHours())}:00`;

                if (visibleDay !== day) {
                  visibleDay = day;
                  isDay = true;
                }

                return (
                  <Time
                    key={index}
                    day={isDay ? day : ""}
                    time={time}
                    num={num}
                    shipGeo={shipGeo}
                    setShipGeo={setShipGeo}
                  />
                );
              });
            })}

            {iceRoute.map((r) => {
              let visibleDay = "";

              return r.routes.map((route, index) => {
                let isDay = false;

                if (num === undefined) {
                  num = 0;
                } else {
                  num++;
                }

                const date = new Date(route.time * 1000);
                const day = `${addZero(date.getDate())}.${addZero(
                  date.getMonth() + 1
                )}`;
                const time = `${addZero(date.getHours())}:00`;

                if (visibleDay !== day) {
                  visibleDay = day;
                  isDay = true;
                }

                return (
                  <Time
                    key={index}
                    day={isDay ? day : ""}
                    time={time}
                    num={num}
                    shipGeo={shipGeo}
                    setShipGeo={setShipGeo}
                  />
                );
              });
            })}
          </ul>
        </div>

        <button
          className="calendar-linear__arrow calendar-linear__arrow_name_left"
          type="button"
          onClick={clickLeft}
          disabled={!isArrowLeft}
        ></button>
        <button
          className="calendar-linear__arrow calendar-linear__arrow_name_right"
          type="button"
          onClick={clickRight}
          disabled={!isArrowRight}
        ></button>
      </div>
    </div>
  );
}

export default MyCalendar;
