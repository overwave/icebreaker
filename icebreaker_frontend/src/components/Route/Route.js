import "./Route.css";
import arrow from "../../images/arrow.svg";
import boat from "../../images/boat-blue.svg";

export default function Route({
  startPointName,
  startDate,
  finishPointName,
  finishDate,
  icebreaker,
  setInfoShip,
}) {
  return (
    <div className="route">
      <div className="route__block">
        <div className="route__point">
          <span className="route__point-name">{startPointName}</span>
          <span className="route__point-date">{startDate}</span>
        </div>
        <div className="route__arrow">
          <img className="route__arrow-img" src={arrow} alt="" />
        </div>
        <div className="route__point">
          <span className="route__point-name">{finishPointName}</span>
          <span className="route__point-date">{finishDate}</span>
        </div>
      </div>

      {icebreaker && icebreaker.icebreakerName !== "" && (
        <div className="route__icebreaker">
          <p className="route__icebreaker-title">
            <img className="route__icebreaker-icon" src={boat} alt="" />
            {icebreaker.icebreakerName}
          </p>
          <p className="route__icebreaker-class">
            {setInfoShip(icebreaker.icebreakerClass)}
          </p>
        </div>
      )}
    </div>
  );
}
