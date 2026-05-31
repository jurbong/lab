import { useEffect, useState } from 'react';
import { authApi, departmentApi, optionApi } from '../api/api';
import { SelectInput, TextInput } from '../components/FormControls';

export function LoginPage({ onLogin, goSignup }) {
  const [form, setForm] = useState({ userId: '', password: '' });
  const [loading, setLoading] = useState(false);

  const change = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const data = await authApi.login(form);
      localStorage.setItem('accessToken', data.accessToken);
      localStorage.setItem('loginUser', JSON.stringify(data));
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
        <button type="button" className="ghost" onClick={goSignup}>회원가입 신청</button>
      </form>
    </div>
  );
}

export function SignupPage({ goLogin }) {
  const [departments, setDepartments] = useState([]);
  const [adminDepartments, setAdminDepartments] = useState([]);
  const [form, setForm] = useState({
    userId: '', password: '', name: '', gender: '', departmentId: '', adminDepartment: '', email: '', phone: '',
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    departmentApi.list().then(setDepartments).catch((e) => alert(e.message));
    optionApi.adminDepartments().then(setAdminDepartments).catch(() => {});
  }, []);

  const change = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const payload = {
        ...form,
        departmentId: form.departmentId ? Number(form.departmentId) : null,
        adminDepartment: form.adminDepartment || null,
      };
      await authApi.signup(payload);
      alert('회원가입 신청이 완료되었습니다. 관리자 승인 후 로그인할 수 있습니다.');
      goLogin();
    } catch (error) {
      alert(error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-wrap">
      <form className="auth-card wide" onSubmit={submit}>
        <h1>회원가입 신청</h1>
        <p>학과/부서는 하나의 선택 항목으로 선택합니다.</p>
        <div className="form-grid">
          <TextInput label="아이디" name="userId" value={form.userId} onChange={change} required />
          <TextInput label="비밀번호" name="password" type="password" value={form.password} onChange={change} required />
          <TextInput label="이름" name="name" value={form.name} onChange={change} required />
          <SelectInput label="성별" name="gender" value={form.gender} onChange={change}>
            <option value="">선택 안 함</option>
            <option value="남">남</option>
            <option value="여">여</option>
          </SelectInput>
          <SelectInput label="학과/부서 - 세부 학과/부서" name="departmentId" value={form.departmentId} onChange={change} required>
            <option value="">선택</option>
            {departments.map((d) => <option key={d.id} value={d.id}>{d.displayName || d.name}</option>)}
          </SelectInput>
          <SelectInput label="관리 부서" name="adminDepartment" value={form.adminDepartment} onChange={change}>
            <option value="">일반 연구실 구성원</option>
            {adminDepartments.map((d) => <option key={d.value} value={d.value}>{d.label}</option>)}
          </SelectInput>
          <TextInput label="이메일" name="email" type="email" value={form.email} onChange={change} required />
          <TextInput label="전화번호" name="phone" value={form.phone} onChange={change} placeholder="010-0000-0000" />
        </div>
        <button disabled={loading}>{loading ? '신청 중...' : '회원가입 신청'}</button>
        <button type="button" className="ghost" onClick={goLogin}>로그인으로 돌아가기</button>
      </form>
    </div>
  );
}
