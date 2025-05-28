import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import styled, { keyframes } from "styled-components";
import { FaPlaneDeparture, FaLock } from "react-icons/fa";
import axios from "axios";

/* ───────────  palette & animation  ─────────── */
const pageBlue = "#d8edff",
  cardBlue = "#b4d0ff",
  inputBlue = "#e6f2ff",
  accent1 = "#175dff",
  accent2 = "#1c8bff",
  textDark = "#003b70";
const fadeIn = keyframes`from{opacity:0;transform:translateY(15px)}
                         to{opacity:1;transform:translateY(0)}`;

/* ───────────  layout  ─────────── */
const Wrapper = styled.div`display:flex;min-height:100vh;font-family:"Segoe UI",Tahoma,Verdana,sans-serif`;
const ImageSide = styled.div`
  flex:1;background:url("https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1352&q=80") center/cover;
  position:relative;&::after{content:"";position:absolute;inset:0;background:rgba(0,0,0,.25)}`;
const FormSide = styled.div`
  flex:1;display:flex;flex-direction:column;align-items:center;justify-content:center;
  padding:3rem 2rem;background:${pageBlue}`;

/* brand */
const Brand = styled.h1`
  display:flex;align-items:center;gap:.5rem;font-size:3rem;font-weight:800;
  letter-spacing:-1px;color:${accent1};margin:0 0 .25rem;
  animation:${fadeIn}.8s ease-out .1s backwards`;
const Tagline = styled.p`
  font-size:1.1rem;color:${textDark};margin-bottom:2.2rem;
  animation:${fadeIn}.8s ease-out .3s backwards`;

/* card */
const Card = styled.div`
  width:100%;max-width:420px;border-radius:20px;padding:2.8rem 2.4rem;
  background:${cardBlue};box-shadow:0 12px 26px rgba(0,59,112,.18);
  animation:${fadeIn}.8s ease-out .5s backwards`;
const Title = styled.h2`text-align:center;color:${textDark};margin-bottom:1.4rem`;

/* inputs/buttons */
const Input = styled.input`
  width:100%;margin-bottom:1.1rem;padding:.9rem 1rem;border-radius:8px;
  border:1px solid rgba(0,0,0,.05);background:${inputBlue};color:${textDark};
  &::placeholder{color:#4d6b9a}`;
const Button = styled.button`
  width:100%;padding:1rem;border:none;border-radius:8px;
  background:linear-gradient(135deg,${accent1},${accent2});
  color:#fff;font-weight:600;cursor:pointer;
  transition:transform .2s,box-shadow .2s;
  &:hover{transform:translateY(-2px);box-shadow:0 8px 22px rgba(23,93,255,.35)}`;

/* message */
const Msg = styled.div`
  margin-bottom:1rem;padding:.85rem 1rem;border-radius:8px;font-size:.95rem;
  background:${p => p.error ? "#ffe6e6" : "#e4ffea"};
  color:${p => p.error ? "#b00020" : "#0e7a29"};
  white-space:pre-line;animation:${fadeIn}.3s ease-out backwards`;

/* ───────────  regex for password rules  ─────────── */
const PASS_PATTERN = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;

/* ───────────  component  ─────────── */
export default function ResetPasswordPage() {
  const { search } = useLocation();
  const navigate = useNavigate();
  const qs = new URLSearchParams(search);
  const email = qs.get("email") || "";
  const token = qs.get("token") || "";

  const [form, setForm] = useState({ newPwd: "", confirm: "" });
  const [msg, setMsg] = useState({ text: "", error: false });
  const onChange = e => setForm({ ...form, [e.target.name]: e.target.value });
  const setError = txt => setMsg({ text: txt, error: true });
  const setGood = txt => setMsg({ text: txt, error: false });

  useEffect(() => {
    const error = localStorage.getItem("error");
    if (error) {
      setError(error);
      localStorage.removeItem("error");
    } else if (!email || !token) {
      setError("Invalid reset link.");
    }
  }, [email, token]);

  const submit = async e => {
    e.preventDefault();
    if (!PASS_PATTERN.test(form.newPwd)) {
      return setError("Password must be ≥8 chars and include upper, lower, digit");
    }
    if (form.newPwd !== form.confirm) {
      return setError("Passwords do not match");
    }

    try {
      console.log("Sending reset request:", { email, token, newPassword: form.newPwd });
      await axios.post("/api/auth/reset-password", {
        email,
        token,
        newPassword: form.newPwd,
      });
      setGood("Password reset! Redirecting to login…");
      setTimeout(() => navigate("/login"), 1800);
    } catch (err) {
      console.error("Reset error:", err);
      setError(err.response?.data?.message || "Reset failed – link may be expired.");
    }
  };

  return (
    <Wrapper>
      <ImageSide />
      <FormSide>
        <Brand>Bookify <FaPlaneDeparture /></Brand>
        <Tagline>Your journey continues – securely</Tagline>

        <Card>
          <Title><FaLock style={{ marginRight: 8 }} />Set new password</Title>
          {msg.text && <Msg error={msg.error}>{msg.text}</Msg>}

          <form onSubmit={submit}>
            <Input
              type="password"
              name="newPwd"
              placeholder="New password"
              required
              value={form.newPwd}
              onChange={onChange}
            />
            <Input
              type="password"
              name="confirm"
              placeholder="Confirm password"
              required
              value={form.confirm}
              onChange={onChange}
            />
            <Button type="submit">Reset password</Button>
          </form>
        </Card>
      </FormSide>
    </Wrapper>
  );
}