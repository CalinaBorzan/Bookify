// src/pages/AccountPage.jsx
import React, { useState, useEffect } from "react";
import styled from "styled-components";
import axios from "axios";
import { FaStar } from "react-icons/fa";

const Wrapper = styled.div`
  max-width: 900px;
  margin: 3rem auto;
  padding: 0 1rem;
`;
const Tabs = styled.div`
  display: flex;
  gap: 2rem;
  border-bottom: 2px solid #e0e0e0;
  margin-bottom: 2rem;
`;
const Tab = styled.button`
  background: none;
  border: none;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  padding: 0.6rem 0;
  color: ${p => p.active === "true" ? "#175dff" : "#777"};
  border-bottom: 3px solid ${p => p.active === "true" ? "#175dff" : "transparent"};
`;
const Section = styled.div`
  animation: fade .3s;
  @keyframes fade { from { opacity: 0 } to { opacity: 1 } }
`;
const FormGroup = styled.div`
  margin-bottom: 1rem;
`;
const Label = styled.label`
  display: block;
  margin-bottom: 0.3rem;
  color: #003b70;
  font-weight: 600;
`;
const Input = styled.input`
  width: 100%;
  padding: 0.6rem;
  border: 1px solid #d1e0ff;
  border-radius: 6px;
`;
const Textarea = styled.textarea`
  width: 100%;
  padding: 0.6rem;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  resize: vertical;
`;
const SaveButton = styled.button`
  padding: 0.7rem 1.5rem;
  background: #175dff;
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  &:hover { background: #004bff; }
`;
const DeleteButton = styled.button`
  padding: 0.7rem 1.5rem;
  background: #e92365;
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  margin-left: 1rem;
  &:hover { background: #ff6b9b; }
`;
const BookingCard = styled.div`
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  padding: 1rem;
  margin-bottom: 1rem;
`;
const CancelBtn = styled.button`
  padding: 0.6rem 1.2rem;
  background: #ffae00;
  color: #fff;
  border: none;
  border-radius: 6px;
  margin-top: 0.5rem;
  cursor: pointer;
  &:hover { background: #ff9100; }
`;
const SubmitReviewButton = styled.button`
  padding: 0.6rem 1.2rem;
  background: #28a745;
  color: #fff;
  border: none;
  border-radius: 6px;
  margin-top: 0.5rem;
  cursor: pointer;
  &:hover { background: #218838; }
`;
const StarButton = styled.span`
  cursor: pointer;
  font-size: 1.4rem;
  margin-right: 0.2rem;
`;
const DownloadBtn = styled.button`
  margin-top: 0.5rem;
  margin-right: 0.5rem;
  padding: 0.4rem 0.8rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  background: #0069d9;
  color: #fff;
  &:hover { background: #0053ba; }
`;

export default function AccountPage() {
  const [tab, setTab] = useState("profile");
  const [user, setUser] = useState(null);
  const [isAdmin, setIsAdmin] = useState(false);
  const [editUser, setEditUser] = useState({});
  const [bookings, setBookings] = useState([]);
  const [payments, setPayments] = useState({});
  const [reviews, setReviews] = useState({});
  const [newReview, setNewReview] = useState({});
  const [newRating, setNewRating] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const cfg = () => ({
    headers: { Authorization: `Bearer ${localStorage.getItem("jwt")}` }
  });

  useEffect(() => {
    const token = localStorage.getItem("jwt");
    if (!token) {
      setError("Please log in.");
      setLoading(false);
      return;
    }

    axios.get("/api/users/me", cfg())
      .then(r => {
        const u = r.data;
        setUser(u);
        const adminFlag = u.role === "ROLE_ADMIN";
        setIsAdmin(adminFlag);
        setEditUser({
          email: u.email,
          username: u.username,
          password: "Current password not shown",
          firstName: u.firstName,
          lastName: u.lastName,
          age: u.age?.toString() || ""
        });
        const url = adminFlag
          ? "/api/bookings/admin/bookings"
          : "/api/bookings/me";
        return axios.get(url, cfg());
      })
      .then(r => {
        setBookings(r.data);
        r.data.forEach(bk => {
          axios.get(`/api/payments/booking/${bk.id}`, cfg())
            .then(res => {
              setPayments(p => ({ ...p, [bk.id]: Array.isArray(res.data) ? res.data : [] }));
            })
            .catch(() => {});
          if (!isAdmin) {
            axios.get(`/api/reviews/booking/${bk.id}`, cfg())
              .then(res => {
                setReviews(p => ({ ...p, [bk.id]: Array.isArray(res.data) ? res.data : [] }));
              })
              .catch(() => {});
          }
        });
      })
      .catch(e => {
        const msg = e.response?.status === 401
          ? "Unauthorized - please log in again"
          : e.message;
        setError(`Load failed: ${msg}`);
      })
      .finally(() => setLoading(false));
  }, []);

  const downloadReceipt = (fmt, bookingId) => {
    axios.get(`/api/payments/booking/${bookingId}`, cfg())
      .then(r => {
        const arr = Array.isArray(r.data) ? r.data : [];
        let content, filename, mime;
        if (fmt === "json") {
          content = JSON.stringify(arr, null, 2);
          filename = `receipt-${bookingId}.json`;
          mime = "application/json";
        } else {
          content = arr.map(p =>
            `Booking ${bookingId}\n` +
            `Amount: €${p.amount}\n` +
            `Transaction ID: ${p.transactionId}\n` +
            `Paid at: ${new Date(p.paidAt).toLocaleString()}\n`
          ).join("\n---\n");
          filename = `receipt-${bookingId}.txt`;
          mime = "text/plain";
        }
        const blob = new Blob([content], { type: mime });
        const link = document.createElement("a");
        link.href = URL.createObjectURL(blob);
        link.download = filename;
        link.click();
      })
      .catch(() => alert("Failed to fetch payments"));
  };

  const handleReviewSubmit = bookingId => {
    if (isAdmin) return;
    const comment = (newReview[bookingId] || "").trim();
    const rating = newRating[bookingId] || 5;
    if (!comment) return;
    axios.post("/api/reviews", { bookingId, comment, rating }, cfg())
      .then(res => {
        setReviews(p => ({
          ...p,
          [bookingId]: [...(p[bookingId] || []), res.data]
        }));
        setNewReview(p => ({ ...p, [bookingId]: "" }));
        setNewRating(p => ({ ...p, [bookingId]: 0 }));
      })
      .catch(e => setError(`Review failed: ${e.message}`));
  };

  const handleCancel = id => {
    if (!window.confirm("Cancel this booking?")) return;
    const call = isAdmin
      ? axios.delete(`/api/bookings/admin/bookings/${id}`, cfg())
      : axios.post(`/api/bookings/${id}/cancel`, null, cfg());
    call
      .then(() => setBookings(bs => bs.filter(b => b.id !== id)))
      .catch(e => setError(`Cancel failed: ${e.message}`));
  };

  const handleSave = () => {
    const payload = {
      email: editUser.email,
      username: editUser.username,
      firstName: editUser.firstName,
      lastName: editUser.lastName,
      age: editUser.age ? parseInt(editUser.age, 10) : undefined
    };
    if (editUser.password !== "Current password not shown") {
      payload.password = editUser.password;
    }
    axios.patch("/api/users/me", payload, cfg())
      .then(r => {
        setUser(r.data);
        alert("Profile updated!");
      })
      .catch(e => setError(`Update failed: ${e.message}`));
  };

  const handleDeleteAccount = () => {
    if (!window.confirm("Delete account?")) return;
    axios.delete("/api/users/me", cfg())
      .then(() => {
        localStorage.removeItem("jwt");
        window.location.href = "/";
      })
      .catch(e => setError(`Delete failed: ${e.message}`));
  };

  if (loading || !user) return <p>Loading…</p>;

  return (
    <Wrapper>
      <h1>My Account</h1>
      <Tabs>
        <Tab active={(tab === "profile").toString()} onClick={() => setTab("profile")}>
          Profile
        </Tab>
        <Tab active={(tab === "bookings").toString()} onClick={() => setTab("bookings")}>
          {isAdmin ? "Bookings on Your Listings" : "My Bookings"}
        </Tab>
      </Tabs>

      {error && <p style={{ color: "#e92365" }}>{error}</p>}

      {/* PROFILE */}
      {tab === "profile" && (
        <Section>
          {[
            ["Email", "email"],
            ["Username", "username"],
            ["Password (change)", "password", "text"],
            ["First name", "firstName"],
            ["Last name", "lastName"],
            ["Age", "age", "number"]
          ].map(([lbl, key, type = "text"]) => (
            <FormGroup key={key}>
              <Label>{lbl}</Label>
              <Input
                type={type}
                value={editUser[key] || ""}
                onChange={e => setEditUser({ ...editUser, [key]: e.target.value })}
              />
            </FormGroup>
          ))}
          <FormGroup>
            <Label>Role</Label>
            <Input value={user.role} disabled />
          </FormGroup>
          <SaveButton onClick={handleSave}>Save changes</SaveButton>
          <DeleteButton onClick={handleDeleteAccount}>Delete account</DeleteButton>
        </Section>
      )}

      {/* BOOKINGS */}
      {tab === "bookings" && (
        <Section>
          <h2>{isAdmin ? "Bookings on Your Listings" : "Your Bookings"}</h2>
          {bookings.length === 0 && <p>No bookings to show.</p>}

          {isAdmin ? (
            // —— ADMIN VIEW ——
            bookings.map(bk => (
              <BookingCard key={bk.id}>
                <h3>{bk.listingTitle} ({bk.type})</h3>
                <p>Check-in: {bk.checkIn} | Check-out: {bk.checkOut}</p>
                <p>Guests: {bk.numGuests}</p>
                <p>Status: {bk.status}</p>
                {bk.listingPrice && (
                  <p>Total ≈ €{(bk.listingPrice * (bk.numGuests || 1)).toFixed(0)}</p>
                )}

                {/* Payments */}
                <div style={{ marginTop: "1rem" }}>
                  <h4>Payments</h4>
                  {payments[bk.id]?.length > 0 ? (
                    payments[bk.id].map(p => (
                      <div key={p.transactionId} style={{ marginBottom: "0.75rem" }}>
                        <p><strong>Amount:</strong> €{p.amount}</p>
                        <p><strong>Transaction ID:</strong> {p.transactionId}</p>
                      </div>
                    ))
                  ) : (
                    <p><em>No payments recorded</em></p>
                  )}
                  <DownloadBtn onClick={() => downloadReceipt("json", bk.id)}>
                    Download JSON
                  </DownloadBtn>
                  <DownloadBtn onClick={() => downloadReceipt("txt", bk.id)}>
                    Download TXT
                  </DownloadBtn>
                </div>

                {/* Who booked */}
                {bk.createdBy && (
                  <div style={{ marginTop: "1rem" }}>
                    <p>
                      Booked by: {bk.createdBy.firstName} {bk.createdBy.lastName}
                      {" "}({bk.createdBy.username})
                    </p>
                    <p>Contact: {bk.createdBy.email}</p>
                  </div>
                )}

                {/* Cancel */}
                {bk.status !== "CANCELLED" && (
                  <CancelBtn onClick={() => handleCancel(bk.id)}>
                    Cancel booking
                  </CancelBtn>
                )}
              </BookingCard>
            ))
          ) : (
            // —— USER VIEW ——
            bookings.map(bk => (
              <BookingCard key={bk.id}>
                <h3>{bk.listingTitle} ({bk.type})</h3>
                <p>Check-in: {bk.checkIn} | Check-out: {bk.checkOut}</p>
                <p>Guests: {bk.numGuests}</p>
                <p>Status: {bk.status}</p>
                {bk.listingPrice && (
                  <p>Total ≈ €{(bk.listingPrice * (bk.numGuests || 1)).toFixed(0)}</p>
                )}

                {/* Payments */}
                <div style={{ marginTop: "1rem" }}>
                  <h4>Payments</h4>
                  {payments[bk.id]?.length > 0 ? (
                    payments[bk.id].map(p => (
                      <div key={p.transactionId} style={{ marginBottom: "0.75rem" }}>
                        <p><strong>Amount:</strong> €{p.amount}</p>
                        <p><strong>Transaction ID:</strong> {p.transactionId}</p>
                      </div>
                    ))
                  ) : (
                    <p><em>No payments recorded</em></p>
                  )}
                  <DownloadBtn onClick={() => downloadReceipt("json", bk.id)}>
                    Download JSON
                  </DownloadBtn>
                  <DownloadBtn onClick={() => downloadReceipt("txt", bk.id)}>
                    Download TXT
                  </DownloadBtn>
                </div>

                {/* Review form */}
                {bk.status !== "CANCELLED" && (
                  <div style={{ marginTop: "1rem" }}>
                    <div>
                      {[1,2,3,4,5].map(n => (
                        <StarButton key={n} onClick={() => setNewRating(r => ({ ...r, [bk.id]: n }))}>
                          <FaStar color={(newRating[bk.id] || 0) >= n ? "#ffc107" : "#e4e5e9"} />
                        </StarButton>
                      ))}
                    </div>
                    <Textarea
                      rows={2}
                      placeholder="Add a review…"
                      value={newReview[bk.id] || ""}
                      onChange={e => setNewReview(r => ({ ...r, [bk.id]: e.target.value }))}
                    />
                    <SubmitReviewButton onClick={() => handleReviewSubmit(bk.id)}>
                      Submit review
                    </SubmitReviewButton>
                  </div>
                )}

                {/* Existing reviews */}
                {reviews[bk.id]?.length > 0 && (
                  <div style={{ marginTop: "1rem" }}>
                    <h4>Reviews</h4>
                    {reviews[bk.id].map(rv => (
                      <div key={rv.id} style={{ marginBottom: "0.5rem" }}>
                        <p>
                          <strong>{rv.author}</strong>: {rv.comment} (<em>{rv.rating}★</em>)
                        </p>
                        {rv.status === "REJECTED" && rv.moderationRemarks && (
                          <p style={{ color: "#e92365" }}>
                            <strong>Moderator:</strong> {rv.moderationRemarks}
                          </p>
                        )}
                      </div>
                    ))}
                  </div>
                )}

                {/* Cancel */}
                {bk.status !== "CANCELLED" && (
                  <CancelBtn onClick={() => handleCancel(bk.id)}>
                    Cancel booking
                  </CancelBtn>
                )}
              </BookingCard>
            ))
          )}
        </Section>
      )}
    </Wrapper>
  );
}
