import "./Time.css";

export default function Time({ num, day, time, setShipGeo, shipGeo }) {
  function handleClick() {
    setShipGeo(num);
  }

  return (
    <li
      className={`time__item ${num === shipGeo ? "time__item_active" : ""} ${
        day !== "" ? "time__item_first" : ""
      }`}
      onClick={handleClick}
    >
      <span className="time__day">{day}</span>
      <span className="time__time">{time}</span>
    </li>
  );
}
