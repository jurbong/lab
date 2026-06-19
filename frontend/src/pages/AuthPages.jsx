import { useState } from 'react';
import { authApi } from '../api/api';
import { TextInput } from '../components/FormControls';

export function LoginPage({ onLogin }) {
  const [form, setForm] = useState({ userId: '', password: '' });
  const [loading, setLoading] = useState(false);

  const change = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const data = await authApi.login(form);
      sessionStorage.setItem('accessToken', data.accessToken);
      sessionStorage.setItem('loginUser', JSON.stringify(data));
      onLogin(data);
    } catch (error) {
      alert(error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
      <div className="auth-wrap">
        <form className="auth-card" onSubmit={submit}>
          <h1>연구실 안전관리 시스템</h1>
          <p>로그인 후 시스템을 이용할 수 있습니다.</p>
          <TextInput label="아이디" name="userId" value={form.userId} onChange={change} required />
          <TextInput label="비밀번호" name="password" type="password" value={form.password} onChange={change} required />
          <button disabled={loading}>{loading ? '로그인 중...' : '로그인'}</button>
        </form>
      </div>
  );
}
