import Application from "../Application/Application";
import ApplicationsError from "../ApplicationsError/ApplicationsError";

export default function ApplicationsConstructor({
  applications,
  text,
  status,
  myClass,
  getShipRoute,
  setInfoShip,
}) {
  if (applications && applications.length) {
    return (
      <>
        {applications.map((item, index) => {
          return (
            <Application
              key={index}
              application={item}
              status={status}
              myClass={myClass}
              getShipRoute={getShipRoute}
              setInfoShip={setInfoShip}
            />
          );
        })}
      </>
    );
  } else {
    return <ApplicationsError text={text} />;
  }
}
