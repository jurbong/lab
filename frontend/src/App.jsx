import { useEffect, useState } from "react";
import Layout from "./components/Layout";
import { LoginPage } from "./pages/AuthPages";
import Dashboard from "./pages/Dashboard";
import UserManagement from "./pages/UserManagement";
import LabManagement from "./pages/LabManagement";
import ChemicalManagement from "./pages/ChemicalManagement";
import WasteManagement from "./pages/WasteManagement";
import InspectionManagement from "./pages/inspection/InspectionManagement";
import InspectionCreate from "./pages/inspection/InspectionCreate";
import EducationManagement from "./pages/EducationManagement";

function App() {
  const [user, setUser] = useState(null);
  const [page, setPage] = useState("dashboard");

  useEffect(() => {
    const saved = localStorage.getItem("loginUser");
    if (saved) {
      try {
        setUser(JSON.parse(saved));
      } catch {
        localStorage.removeItem("loginUser");
      }
    }
  }, []);

  const logout = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("loginUser");
    setUser(null);
    setPage("dashboard");
  };

  if (!user) {
    return <LoginPage onLogin={setUser} />;
  }

  const PAGES = {
    INSPECTIONS: "inspections",
    INSPECTION_CREATE: "inspectionCreate",
  };

  const renderPage = () => {
    switch (page) {
      case "users":
        return <UserManagement user={user} />;
      case "labs":
        return <LabManagement user={user} />;
      case "chemicals":
        return <ChemicalManagement user={user} />;
      case "wastes":
        return <WasteManagement user={user} />;
      case PAGES.INSPECTIONS:
        return (
          <InspectionManagement user={user} setPage={setPage} PAGES={PAGES} />
        );
      case PAGES.INSPECTION_CREATE:
        return <InspectionCreate user={user} setPage={setPage} PAGES={PAGES} />;
      case "education":
        return <EducationManagement user={user} />;
      default:
        return <Dashboard user={user} />;
    }
  };

  return (
    <Layout user={user} page={page} setPage={setPage} onLogout={logout}>
      {renderPage()}
    </Layout>
  );
}

export default App;
