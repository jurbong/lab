import { useEffect, useState } from "react";
import Layout from "./components/Layout";
import { LoginPage, SignupPage } from "./pages/AuthPages";
import Dashboard from "./pages/Dashboard";
import UserManagement from "./pages/UserManagement";
import LabManagement from "./pages/LabManagement";
import ChemicalManagement from "./pages/ChemicalManagement";
import WasteManagement from "./pages/WasteManagement";
import InspectionManagement from "./pages/inspection/InspectionManagement";
import InspectionCreate from "./pages/inspection/InspectionCreate";
import EducationManagement from "./pages/EducationManagement";

function App() {
  const [authMode, setAuthMode] = useState("login");
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
    return authMode === "signup" ? (
      <SignupPage goLogin={() => setAuthMode("login")} />
    ) : (
      <LoginPage onLogin={setUser} goSignup={() => setAuthMode("signup")} />
    );
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
        0;
        return <InspectionCreate user={user} />;
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
