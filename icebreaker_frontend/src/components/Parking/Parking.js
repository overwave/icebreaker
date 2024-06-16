import { months } from "../../configs/constants";
import "./Parking.css";

export default function Parking({ pointName, startDate, finishDate }) {
  const datesText = `Стоянка с ${getDate(startDate)} по ${getDate(finishDate)}`;

  function getDate(date) {
    const newDate = new Date(date);
    return `${newDate.getDate()} ${months[newDate.getMonth()]}`;
  }

  return (
    <li className="parking">
      <p className="parking__point">{pointName}</p>
      <p className="parking__dates">{datesText}</p>
    </li>
  );
}
