import { useEffect, useState } from "react";
import arrow from "../../images/arrow-down-grey.svg";

function SelectShips({
  options,
  selectedOption,
  changeOption,
  name,
  setSelected,
  myClass,
  clue,
  setInfoShip,
  children,
}) {
  /* options должен быть вида [{id: id, name: name}] */

  const [isOpen, setIsOpen] = useState(false);

  useEffect(() => {
    if (isOpen) {
      window.addEventListener("click", closeSelect);
      return () => window.removeEventListener("click", closeSelect);
    }
  }, [isOpen]);

  function toggleSelect() {
    setIsOpen(!isOpen);
  }

  function closeSelect(e) {
    if (
      e.target.classList[0] !== "my-select__list" &&
      e.target.classList[0] !== "my-select__select" &&
      e.target.className !== "my-select__text" &&
      e.target.classList[0] !== "my-select__arrow"
    ) {
      setIsOpen(false);
    }
  }

  return (
    <div className={`my-select ${myClass}`}>
      <div
        className={`my-select__select ${
          options[selectedOption] ? "my-select__select_active" : ""
        }`}
        onClick={toggleSelect}
      >
        <span className="my-select__text">
          {options[selectedOption] ? options[selectedOption].name : clue}
        </span>
        {options[selectedOption] && (
          <span className="my-select__clue">{clue}</span>
        )}
        <img
          className={`my-select__arrow ${
            isOpen ? "my-select__arrow_opened" : ""
          }`}
          alt="Открыть"
          src={arrow}
        />
      </div>
      <ul
        className={`my-select__list ${isOpen ? "my-select__list_opened" : ""}`}
      >
        {options.map((option, index) => {
          const info = setInfoShip(option.iceClass, option.speed);
          function handleOption() {
            changeOption(index, name, setSelected);
          }
          return (
            <li
              key={index}
              className={`my-select__item my-select__item_type_ships ${
                selectedOption === index ? "my-select__item_active" : ""
              }`}
              onClick={handleOption}
            >
              {option.name}
              <p className="my-select__info">{info}</p>
            </li>
          );
        })}

        {children}
      </ul>
    </div>
  );
}

export default SelectShips;
