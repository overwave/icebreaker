import "./Popup.css";
import close from "../../images/close-popup.svg";

export default function Popup({ title, isOpen, onClose, children }) {
    if (isOpen) {
        return (
            <div className="popup">
                <button className="popup__close" type="button" onClick={onClose}>
                    <img className="popup__close-img" src={close} alt="Закрыть" />
                </button>
                <div className="popup__container">
                    <h2 className="popup__title">{title}</h2>
                    {children}
                </div>
            </div>
        );
    }
    
}