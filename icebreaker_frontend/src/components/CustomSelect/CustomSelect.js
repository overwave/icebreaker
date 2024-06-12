import { useEffect, useState } from "react";
import "./CustomSelect.css";
import arrow from "../../images/arrow-down-grey.svg";

function CustomSelect({ options, selectedOption, changeOption, name, setSelected, myClass, clue }) {

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
            <div className={`my-select__select ${options[selectedOption] ? "my-select__select_active":""}`} onClick={toggleSelect}>
                <span className="my-select__text">{options[selectedOption] ? options[selectedOption].name:clue}</span>
                {options[selectedOption] && <span className="my-select__clue">{clue}</span>}
                <img className={`my-select__arrow ${isOpen ? "my-select__arrow_opened":""}`} alt="Открыть" src={arrow} />
            </div>
            <ul className={`my-select__list ${isOpen ? "my-select__list_opened":""}`}>
                {options.map((option, index) => {
                    function handleOption() {
                        changeOption(index, name, setSelected);
                    }
                    return (
                        <li key={index} className={`my-select__item ${selectedOption === index ? "my-select__item_active":""}`} onClick={handleOption}>{option.name}</li>
                    );
                })}
            </ul>
        </div>
    );
}

export default CustomSelect;
