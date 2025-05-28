import React, { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import styled, { keyframes } from "styled-components";

const fadeIn = keyframes`
  from { opacity:0; transform:translateY(10px); }
  to   { opacity:1; transform:translateY(0); }
`;

const Wrapper = styled.div`
  max-width:800px;
  margin:2rem auto;
  padding:0 1.5rem;
  font-family:"Segoe UI",sans-serif;
  animation:${fadeIn} .5s ease-out;
`;

const Header = styled.div`margin-bottom:1.5rem;`;
const Title = styled.h1`margin:0 0 1rem;color:#003b70;`;
const Detail = styled.p`margin:.5rem 0;`;

const BookBtn = styled.button`
  display:block;
  margin:1.5rem auto;
  padding:.9rem 2rem;
  background:#175dff;
  color:#fff;
  border:none;border-radius:30px;
  cursor:pointer;font-weight:600;
`;

const ModalBG = styled.div`
  position:fixed;inset:0;
  background:rgba(0,0,0,.5);
  display:${p => p.open?"flex":"none"};
  align-items:center;justify-content:center;z-index:1000;
`;
const ModalContent = styled.div`
  background:#fff;padding:2rem;border-radius:10px;
  max-width:400px;width:90%;text-align:center;
`;

const Reviews = styled.div`margin-top:2rem;`;
const Review = styled.div`padding:.75rem;border-bottom:1px solid #eee;`;
const AddReview = styled.textarea`
  width:100%;min-height:80px;padding:.5rem;margin-top:1rem;
`;
const SubmitReview = styled.button`
  margin-top:.5rem;padding:.6rem 1.2rem;
  background:#28a745;color:#fff;border:none;border-radius:6px;
`;

export default function BookPage() {
  const { state } = useLocation();
  const { item, window } = state || {};
  const [modalOpen, setModalOpen] = useState(false);
  const [reviews, setReviews] = useState([]);
  const [newReview, setNewReview] = useState("");

  useEffect(() => {
    if (!item) return;
    // fetch reviews
    fetch(`/api/${item.listingType.toLowerCase()}/${item.id}/reviews`, {
      headers: { Authorization: `Bearer ${localStorage.getItem("jwt")}` }
    })
      .then(r => r.json())
      .then(setReviews)
      .catch(console.error);
  }, [item]);

  if (!item) return <Wrapper>No item selected.</Wrapper>;

  const handleBooking = payNow => {
    // similar to package: post booking for this single item
    const [from, to] = window.split(" – ");
    const dto = {
      userId: 1, // replace
      listingId: item.id,
      type: item.listingType,
      checkIn: from || null,
      checkOut: to || null,
      numGuests: 1,
      payNow
    };
    fetch("/api/bookings", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${localStorage.getItem("jwt")}`
      },
      body: JSON.stringify(dto)
    })
      .then(r => {
        if (!r.ok) throw new Error(r.statusText);
        alert("Booking confirmed!");
        setModalOpen(false);
      })
      .catch(err => alert(err.message));
  };

  const submitReview = () => {
    fetch(`/api/${item.listingType.toLowerCase()}/${item.id}/reviews`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${localStorage.getItem("jwt")}`
      },
      body: JSON.stringify({ text: newReview })
    })
      .then(r => r.json())
      .then(r => {
        setReviews(prev => [r, ...prev]);
        setNewReview("");
      })
      .catch(console.error);
  };

  return (
    <Wrapper>
      <Header>
        <Title>{item.title}</Title>
        <Detail>{item.description}</Detail>
        {window && <Detail><strong>Dates:</strong> {window}</Detail>}
      </Header>

      <BookBtn onClick={() => setModalOpen(true)}>Book Now</BookBtn>

      <ModalBG open={modalOpen}>
        <ModalContent>
          <h3>Choose Payment Option</h3>
          <BookBtn onClick={() => handleBooking(true)}>Pay Now</BookBtn>
          <BookBtn onClick={() => handleBooking(false)}>Pay on Arrival</BookBtn>
          <BookBtn onClick={() => setModalOpen(false)}>Cancel</BookBtn>
        </ModalContent>
      </ModalBG>

      <Reviews>
        <h4>Reviews</h4>
        {reviews.map(r => (
          <Review key={r.id}>{r.text}</Review>
        ))}

        <AddReview
          value={newReview}
          onChange={e => setNewReview(e.target.value)}
          placeholder="Write your review..."
        />
        <SubmitReview onClick={submitReview}>Submit Review</SubmitReview>
      </Reviews>
    </Wrapper>
  );
}
