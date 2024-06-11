import "./Applications.css";
import arrow from "../../images/arrow-down.svg";
import { useState } from "react";
import Application from "../Application/Application";

export default function Applications({ getShips }) {

    const [isOpen, setIsOpen] = useState(true);

    function toggleMenu() {
        //getShips();
        setIsOpen(!isOpen);
    }

    return (
        <div className={`applications ${isOpen ? "":"applications_closed"}`}>
            <div className="applications__header" onClick={toggleMenu}>
                <h2 className="applications__title">Мои заявки</h2>
                <img className={`applications__arrow ${isOpen ? "":"applications__arrow_closed"}`} slt="" src={arrow} />
            </div>

            {isOpen && 
                <div className="applications__content">
                    <div className="applications__body">
                        <nav className="applications__nav">
                            <ul className="applications__nav-list">
                                <li className="applications__nav-item applications__nav-item_active">В маршруте</li>
                                <li className="applications__nav-item">В обработке</li>
                                <li className="applications__nav-item">Архив</li>
                            </ul>
                        </nav>

                        <ul className="applications__list">
                            <Application />
                        </ul>
                    </div>

                    <div className="applications__footer">
                        <button className="applications__btn" type="button">Подать новую заявку</button>
                    </div>
                </div>
            }
        </div>
    );
}