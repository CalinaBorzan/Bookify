import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import styled, { keyframes } from "styled-components";
import axios from "axios";
import { FaBolt } from "react-icons/fa";
import Header from "../components/Header";

/* --- palette & animations --- */
const pageBlue = "#d8edff";
const fadeIn = keyframes`from{opacity:0;transform:translateY(15px)}to{opacity:1;transform:translateY(0)}`;
const zoom = keyframes`from{transform:scale(1)}to{transform:scale(1.08)}`;
const textDark = "#003b70";
const accent1 = "#175dff";

/* --- styled components --- */
const Wrapper = styled.div`
  min-height: 100vh;
  background: ${pageBlue};
  display: flex;
  flex-direction: column;
  font-family: "Segoe UI", Tahoma, Verdana, sans-serif;
`;
const Hero = styled.section`
  background: url("https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1600&auto=format") center/cover fixed;
  color: #fff;
  text-align: center;
  padding: 4.5rem 1rem 6rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  animation: ${fadeIn} .7s ease-out;
`;
const HeroTitle = styled.h1`
  font-size: 3rem;
  font-weight: 800;
  margin: 0 0 1rem;
`;
const Grid = styled.section`
  width: 100%;
  max-width: 1400px;
  margin: -4rem auto 0;
  display: grid;
  gap: 2rem;
  padding: 0 2.5rem 4rem;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  animation: ${fadeIn} .8s ease-out;
`;
const Card = styled.div`
  position: relative;
  border-radius: 18px;
  overflow: hidden;
  height: 260px;
  box-shadow: 0 8px 18px rgba(0,0,0,.15);
  transition: transform .3s;
  background: #b4d0ff;
  &:hover { transform: translateY(-6px); }
  &:hover img { animation: ${zoom} 5s ease-out forwards; }
`;
const Img = styled.img`
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: ${p => p.src ? "block" : "none"};
`;
const Mask = styled.div`
  position: absolute;
  inset: 0;
  background: linear-gradient(to bottom, transparent 42%, rgba(0,0,0,.65));
  pointer-events: none;
`;
const Label = styled.h3`
  position: absolute;
  bottom: 1.2rem;
  left: 1.4rem;
  margin: 0;
  color: #fff;
  font-size: 1.5rem;
  font-weight: 600;
`;
const CardContent = styled.div`
  position: relative;
  z-index: 2;
  padding: 1rem;
  color: ${textDark};
`;
const CardTitle = styled.h3`
  margin: 0 0 0.5rem;
  font-size: 1.2rem;
  display: flex;
  align-items: center;
  gap: 0.4rem;
`;
const CardDetail = styled.p`
  margin: 0.25rem 0;
  font-size: 0.95rem;
`;
const Button = styled.button`
  padding: 0.6rem 1.2rem;
  background: ${accent1};
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  margin: 0.5rem 0.5rem 0 0;
  &:hover { background: #004bff; }
`;

export default function AdminPage() {
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("jwt");
    if (!token) {
      setError("No auth token. Redirecting...");
      setTimeout(() => navigate("/"), 1500);
      return;
    }
    axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
       axios.get("/api/reviews/pending")
    .then(res => {
      // only keep reviews that have NOT been moderated and have no moderator remark
      const pending = res.data.filter(r =>
        r.moderated === false &&
        (r.moderationRemarks == null || r.moderationRemarks.trim() === "")
      );
      setReviews(pending);
      setLoading(false);
    })
      .catch(err => {
        setError(`Failed to load: ${err.response?.status===401 ? "Unauthorized" : err.message}`);
        setLoading(false);
        if (err.response?.status===401) setTimeout(() => navigate("/"), 1500);
      });
  }, [navigate]);

  const handleApprove = id => moderateReview(id, true, null);
  const handleReject = id => {
    const remarks = window.prompt("Enter rejection reason:");
    if (remarks != null) moderateReview(id, false, remarks);
  };

  const moderateReview = async (id, moderated, remarks) => {
    const token = localStorage.getItem("jwt");
    try {
      await axios.post(
        `/api/reviews/${id}/moderate`,
        null,
        {
          headers: { Authorization: `Bearer ${token}` },
          params: { approved: moderated, remarks: remarks }
        }
      );
      setReviews(prev => prev.filter(r => r.id !== id));
    } catch (err) {
      setError(`Moderation failed: ${err.message}`);
    }
  };

  if (loading) {
    return (
      <Wrapper>
        <Header />
        <p style={{ textAlign: "center", marginTop: "3rem" }}>Loading…</p>
      </Wrapper>
    );
  }

  return (
    <Wrapper>
      <Header />
      <Hero>
        <HeroTitle>Moderate Reviews</HeroTitle>
        <p style={{ fontSize: "1.15rem", maxWidth: 640 }}>
          Approve or reject traveller feedback.
        </p>
      </Hero>
      <Grid>
        {reviews.length === 0 && (
          <p style={{ gridColumn: "1/-1", textAlign: "center", fontSize: "1.15rem" }}>
            No pending reviews 🎉
          </p>
        )}
        {reviews.map(r => (
          <Card key={r.id}>
            {r.listingImage && <Img src={r.listingImage} alt={r.listingTitle} />}
            <Mask />
            <Label>{r.listingTitle}</Label>
            <CardContent>
              <CardTitle>
                {r.listingTitle}
                <FaBolt />
                {r.rating}★
              </CardTitle>
              <CardDetail>{r.comment}</CardDetail>
              <CardDetail style={{ fontStyle: 'italic', marginTop: '0.5rem' }}>
                by {r.author}
              </CardDetail>
              <Button onClick={() => handleApprove(r.id)}>Approve</Button>
              <Button onClick={() => handleReject(r.id)}>Reject</Button>
            </CardContent>
          </Card>
        ))}
      </Grid>
      {error && (
        <p style={{ color: "#e92365", textAlign: "center", margin: "2rem" }}>{error}</p>
      )}
    </Wrapper>
  );
}
