import { useEffect, useState } from 'react';
import Layout from './components/Layout';
import { LoginPage, SignupPage } from './pages/AuthPages';
import Dashboard from './pages/Dashboard';
import UserManagement from './pages/UserManagement';
import LabManagement from './pages/LabManagement';
import ChemicalManagement from './pages/ChemicalManagement';
import WasteManagement from './pages/WasteManagement';
import InspectionManagement from './pages/InspectionManagement';
import EducationManagement from './pages/EducationManagement';

function App() {
  const [authMode, setAuthMode] = useState('login');
  const [user, setUser] = useState(null);
  const [page, setPage] = useState('dashboard');

  useEffect(() => {
    const saved = localStorage.getItem('loginUser');
    if (saved) {
      try { setUser(JSON.parse(saved)); } catch { localStorage.removeItem('loginUser'); }
    }
  }, []);

  const logout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('loginUser');
    setUser(null);
    setPage('dashboard');
  };

  if (!user) {
    return authMode === 'signup'
      ? <SignupPage goLogin={() => setAuthMode('login')} />
      : <LoginPage onLogin={setUser} goSignup={() => setAuthMode('signup')} />;
  }

  const renderPage = () => {
    switch (page) {
      case 'users': return <UserManagement />;
      case 'labs': return <LabManagement user={user} />;
      case 'chemicals': return <ChemicalManagement user={user} />;
      case 'wastes': return <WasteManagement user={user} />;
      case 'inspections': return <InspectionManagement user={user} />;
      case 'education': return <EducationManagement user={user} />;
      default: return <Dashboard user={user} />;
    }
  };

  return <Layout user={user} page={page} setPage={setPage} onLogout={logout}>{renderPage()}</Layout>;
}

export default App;
