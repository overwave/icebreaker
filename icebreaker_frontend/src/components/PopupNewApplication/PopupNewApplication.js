import { useEffect, useState } from "react";
import CustomSelect from "../CustomSelect/CustomSelect";
import Popup from "../Popup/Popup";
import { errorsApplication } from "../../configs/errors";
import Calendar from "react-calendar";
import "react-calendar/dist/Calendar.css";
import add from "../../images/add.svg";
import SelectShips from "../SelectShips/SelectShips";

export default function PopupNewApplication({
  onClose,
  changeOption,
  navPoints,
  setNewApplication,
  getShips,
  ships,
  setInfoShip,
  openPopupShip,
  addZero,
}) {
  const [selectedShip, setSelectedShip] = useState(undefined);
  const [selectedStartPoint, setSelectedStartPoint] = useState(undefined);
  const [selectedFinishPoint, setSelectedFinishPoint] = useState(undefined);
  const [selectedDate, setSelectedDate] = useState(undefined);
  const [myDate, setMyDate] = useState("");
  const [isCalendar, setIsCalendar] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [isDisabled, setIsDisabled] = useState(true);

  useEffect(() => {
    getShips();
  }, []);

  useEffect(() => {
    if (selectedDate) {
      setDate();
    }
  }, [selectedDate]);

  useEffect(() => {
    if (
      selectedShip !== undefined &&
      selectedStartPoint !== undefined &&
      selectedFinishPoint !== undefined &&
      selectedDate !== undefined
    ) {
      setIsDisabled(false);
    } else {
      setIsDisabled(true);
    }
  }, [selectedShip, selectedStartPoint, selectedFinishPoint, selectedDate]);

  function toggleCalendar() {
    setIsCalendar(!isCalendar);
  }

  function setDate() {
    toggleCalendar();
    const newDate = `${addZero(selectedDate.getDate())}.${addZero(
      selectedDate.getMonth() + 1
    )}.${selectedDate.getFullYear()}`;
    setMyDate(newDate);
  }

  function onSubmit(e) {
    e.preventDefault();

    if (selectedStartPoint === selectedFinishPoint) {
      setErrorMessage(errorsApplication.points);
      return;
    }

    const date = `${selectedDate.getFullYear()}-${addZero(
      selectedDate.getMonth() + 1
    )}-${addZero(selectedDate.getDate())}`;
    const values = {
      shipId: ships[selectedShip].id,
      startDate: date,
      startPointId: navPoints[selectedStartPoint].id,
      finishPointId: navPoints[selectedFinishPoint].id,
    };

    setNewApplication(values);
  }

  return (
    <Popup title="Заявка на проводку" onClose={onClose} myClass="application">
      <form
        className="form form-application"
        name="form_application"
        onSubmit={onSubmit}
      >
        <div className="form__field">
          <SelectShips
            myClass="form-application__select"
            options={ships}
            selectedOption={selectedShip}
            changeOption={changeOption}
            name="selectedShip"
            setSelected={setSelectedShip}
            clue="Судно"
            setInfoShip={setInfoShip}
          >
            <li className="my-select__button" onClick={openPopupShip}>
              <img className="my-select__button-img" src={add} alt="" />
              Добавить новое судно
            </li>
          </SelectShips>
        </div>

        <div className="form__field">
          <label
            className={`form__label ${
              myDate === "" ? "" : "form__label_active"
            }`}
          >
            Дата отплытия
          </label>
          <input
            name="login"
            className={`form__input ${
              myDate === "" ? "" : "form__input_active"
            }`}
            type="text"
            disabled={true}
            value={myDate}
            required
          />
          <svg
            onClick={toggleCalendar}
            className="form__calendar-icon"
            width="16"
            height="16"
            viewBox="0 0 16 16"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M5 8C4.44772 8 4 8.44771 4 9V10C4 10.5523 4.44772 11 5 11H7C7.55228 11 8 10.5523 8 10V9C8 8.44772 7.55228 8 7 8H5Z"
              fill="#80858E"
            />
            <path
              fillRule="evenodd"
              clipRule="evenodd"
              d="M5 0C5.55228 0 6 0.447715 6 1H10C10 0.447715 10.4477 0 11 0C11.5523 0 12 0.447715 12 1H13C14.6569 1 16 2.34315 16 4V13C16 14.6569 14.6569 16 13 16H3C1.34315 16 0 14.6569 0 13V4C0 2.34315 1.34315 1 3 1H4C4 0.447715 4.44772 0 5 0ZM3 3C2.44772 3 2 3.44772 2 4V5H12C12.5523 5 13 5.44772 13 6C13 6.55228 12.5523 7 12 7H2V13C2 13.5523 2.44772 14 3 14H13C13.5523 14 14 13.5523 14 13V4C14 3.44772 13.5523 3 13 3H3Z"
              fill="#80858E"
            />
          </svg>
          {isCalendar && (
            <Calendar
              className="form__calendar"
              onChange={setSelectedDate}
              value={selectedDate}
              minDate={new Date("2024-03-01")}
              maxDate={new Date("2024-05-31")}
              defaultActiveStartDate={new Date("2024-03-01")}
            />
          )}
        </div>

        <div className="form__double-field">
          <CustomSelect
            myClass="form-application__select"
            options={navPoints}
            selectedOption={selectedStartPoint}
            changeOption={changeOption}
            name="selectedStartPoint"
            setSelected={setSelectedStartPoint}
            clue="Пункт отплытия"
          />
          <CustomSelect
            myClass="form-application__select"
            options={navPoints}
            selectedOption={selectedFinishPoint}
            changeOption={changeOption}
            name="selectedFinishPoint"
            setSelected={setSelectedFinishPoint}
            clue="Пункт прибытия"
          />
        </div>

        {errorMessage !== "" && (
          <div className="form__error">{errorMessage}</div>
        )}

        <div className="form__btns">
          <button
            className="form__btn content__btn_secondary"
            type="button"
            onClick={onClose}
          >
            Отменить
          </button>
          <button
            className={`form__btn content__btn ${
              isDisabled ? "content__btn_disabled" : ""
            }`}
            type="submit"
            disabled={isDisabled}
          >
            Создать
          </button>
        </div>
      </form>
    </Popup>
  );
}
