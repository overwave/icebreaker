import { useEffect, useState } from "react";
import Popup from "../Popup/Popup";
import CustomSelect from "../CustomSelect/CustomSelect";
import { errorsShips } from "../../configs/errors";

export default function PopupNewShip({
  onClose,
  iceClasses,
  getIceClasses,
  changeOption,
  setIceClass,
  setNewShip,
}) {
  const [shipName, setShipName] = useState("");
  const [shipSpeed, setShipSpeed] = useState("");
  const [selectedIceClass, setSelectedIceClass] = useState(undefined);
  const [isErrorName, setIsErrorName] = useState(false);
  const [isErrorSpeed, setIsErrorSpeed] = useState(false);
  const [isDisabled, setIsDisabled] = useState(true);

  useEffect(() => {
    getIceClasses();
  }, []);

  useEffect(() => {
    if (shipName !== "" && shipSpeed !== "" && selectedIceClass !== undefined) {
      setIsDisabled(false);
    } else {
      setIsDisabled(true);
    }
  }, [shipName, shipSpeed, selectedIceClass]);

  function handleChangeName(e) {
    setShipName(e.target.value);
  }

  function handleChangeSpeed(e) {
    setShipSpeed(e.target.value);
  }

  function onSubmit(e) {
    e.preventDefault();

    let error = false;

    if (shipName.length < 2) {
      error = true;
      setIsErrorName(true);
    } else {
      setIsErrorName(false);
    }

    if (!Number(shipSpeed) || Number(shipSpeed) < 5 || Number(shipSpeed) > 25) {
      error = true;
      setIsErrorSpeed(true);
    } else {
      setIsErrorSpeed(false);
    }

    if (!error) {
      const res = {
        name: shipName,
        speed: Number(shipSpeed),
        iceClass: iceClasses[selectedIceClass].name,
      };
      setNewShip(res);
    }
  }

  return (
    <Popup title="Новое судно" onClose={onClose} myClass="ship">
      <form className="form form-ship" name="form_ship" onSubmit={onSubmit}>
        <div className="form__field">
          <label
            className={`form__label ${
              shipName === "" ? "" : "form__label_active"
            }`}
            htmlFor="ship-name"
          >
            Название судна
          </label>
          <input
            id="ship-name"
            name="ship-name"
            className={`form__input ${
              shipName === "" ? "" : "form__input_active"
            }`}
            type="text"
            onChange={handleChangeName}
            value={shipName}
            min={2}
            required
          />
          {isErrorName && (
            <span className="form__input-error">{errorsShips.name || ""}</span>
          )}
        </div>

        <div className="form__field">
          <CustomSelect
            myClass="form-ship__select"
            options={iceClasses}
            selectedOption={selectedIceClass}
            changeOption={changeOption}
            name="selectedIceClass"
            setSelected={setSelectedIceClass}
            clue="Ледовый класс"
            setIceClass={setIceClass}
          />
        </div>

        <div className="form__field">
          <label
            className={`form__label ${
              shipSpeed === "" ? "" : "form__label_active"
            }`}
            htmlFor="ship-speed"
          >
            Скорость, узлы (по чистой воде)
          </label>
          <input
            name="ship-speed"
            id="ship-speed"
            className={`form__input ${
              shipSpeed === "" ? "" : "form__input_active"
            }`}
            type="text"
            onChange={handleChangeSpeed}
            value={shipSpeed}
            min="5"
            max="25"
            required
          />
          {isErrorSpeed && (
            <span className="form__input-error">{errorsShips.speed || ""}</span>
          )}
        </div>

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
            Добавить
          </button>
        </div>
      </form>
    </Popup>
  );
}
