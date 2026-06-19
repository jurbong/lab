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

function isTokenValid(token) {
  if (!token) return false;
  try {
    const base64 = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/");
    const { exp } = JSON.parse(atob(base64));
    return typeof exp === "number" && exp * 1000 > Date.now();
  } catch {
    return false;
  }
}

function clearAuth() {
  sessionStorage.removeItem("accessToken");
  sessionStorage.removeItem("loginUser");
}

function App() {
  const [user, setUser] = useState(null);
  const [page, setPage] = useState("dashboard");

  useEffect(() => {
    const saved = sessionStorage.getItem("loginUser");
    const token = sessionStorage.getItem("accessToken");
    if (saved && isTokenValid(token)) {
      try {
        setUser(JSON.parse(saved));
      } catch {
        clearAuth();
      }
    } else {
      clearAuth();
    }
  }, []);

  const logout = () => {
    clearAuth();
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
