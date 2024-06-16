import "./Application.css";
import boat from "../../images/boat.svg";
import download from "../../images/download.svg";
import { useEffect, useState } from "react";
import { months } from "../../configs/constants";
import Route from "../Route/Route";

export default function Application({
  application,
  status,
  myClass,
  getShipRoute,
  setInfoShip,
}) {
  const [dates, setDates] = useState({
    start: "",
    finish: "",
  });
  const [activeApplication, setActiveApplication] = useState(
    localStorage.getItem("activeApplication")
      ? Number(localStorage.getItem("activeApplication"))
      : undefined
  );

  useEffect(() => {
    if (status === "pending" || status === "archive") {
      getDate(application.startDate, application.finishDate, true);
    }
  }, []);

  function getDate(start, finish, isReturn = false) {
    const startDate = new Date(start);
    const finishDate = finish ? new Date(finish) : "";
    const newDates = {
      start: `${startDate.getDate()} ${months[startDate.getMonth()]}`,
      finish: finishDate
        ? `${finishDate.getDate()} ${months[finishDate.getMonth()]}`
        : "",
    };

    if (isReturn) {
      setDates(newDates);
    } else {
      return newDates;
    }
  }

  function handleRoute(e) {
    if (status === "agreed" && e.target.className !== "application__icon") {
      setActiveApplication(application.id);
      localStorage.setItem("activeApplication", application.id);

      getShipRoute({ id: application.id });
    }
  }

  if (application) {
    return (
      <li
        className={`application ${myClass ? `application-${myClass}` : ""} ${
          activeApplication === application.id ? "application_active" : ""
        }`}
        onClick={handleRoute}
      >
        {myClass !== "icebreaker" && (
          <div className="application__header">
            {application.convoy && (
              <div className="application__icebreaker">
                <img
                  className="application__icebreaker-icon"
                  src={boat}
                  alt=""
                />
                Под сопровождением
              </div>
            )}

            <div className="application__header-block">
              <div className="application__header-left">
                <p className="application__title">{application.shipName}</p>
                <p className="application__points">
                  {setInfoShip
                    ? setInfoShip(application.shipClass, application.speed)
                    : application.shipClass}
                </p>
              </div>
              {/* <div className="application__btns">
                <button
                  className="application__btn application__btn_name_download"
                  type="button"
                >
                  <img
                    className="application__icon"
                    src={download}
                    alt="Скачать заявку"
                  />
                </button>
              </div> */}
            </div>
          </div>
        )}

        {status === "agreed" &&
          application.routes.map((route, index) => {
            const routeDates = getDate(
              route.startDate,
              route.finishDate,
              false
            );
            return (
              <Route
                key={index}
                startPointName={route.startPointName}
                startDate={routeDates.start}
                finishPointName={route.finishPointName}
                finishDate={routeDates.finish}
                icebreaker={{
                  icebreakerName: route.icebreakerName,
                  icebreakerClass: route.icebreakerClass,
                }}
                setInfoShip={setInfoShip}
              />
            );
          })}

        {status === "pending" && (
          <Route
            startPointName={application.startPointName}
            startDate={dates.start}
            finishPointName={application.finishPointName}
            finishDate={dates.finish}
          />
        )}

        {status === "archive" && (
          <Route
            startPointName={application.startPointName}
            startDate={dates.start}
            finishPointName={application.finishPointName}
            finishDate={dates.finish}
          />
        )}
      </li>
    );
  }
}
