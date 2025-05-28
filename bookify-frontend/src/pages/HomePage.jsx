import React from "react";
import { useNavigate } from "react-router-dom";
import styled, { keyframes } from "styled-components";
import Header from "../components/Header";      // ✅ new

/* --- simple palette / animations --- */
const pageBlue = "#d8edff";
const fadeIn = keyframes`from{opacity:0;transform:translateY(15px)}
                         to  {opacity:1;transform:translateY(0)}`;
const zoom   = keyframes`from{transform:scale(1)}
                         to  {transform:scale(1.08)}`;

/* --- layout --- */
const Wrapper = styled.div`
  min-height: 100vh;
  background: ${pageBlue};
  display: flex;
  flex-direction: column;
  font-family: "Segoe UI", Tahoma, Verdana, sans-serif;
`;

/* hero */
const Hero = styled.section`
  background: url("https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1600&auto=format")
              center/cover fixed;
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

/* grid */
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
  cursor: pointer;
  box-shadow: 0 8px 18px rgba(0,0,0,.15);
  transition: transform .3s;
  &:hover      { transform: translateY(-6px); }
  &:hover img  { animation: ${zoom} 5s ease-out forwards; }
`;
const Img  = styled.img`width:100%;height:100%;object-fit:cover;`;
const Mask = styled.div`
  position: absolute;
  inset: 0;
  background: linear-gradient(to bottom,transparent 42%,rgba(0,0,0,.65));
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

export default function HomePage() {
  const go = useNavigate();

  const dest = [
    { c: "FR", n: "France",   img: "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=800&auto=format" },
    { c: "ES", n: "Spain",    img: "/destinations/barcelona.jpg" },
    { c: "IT", n: "Italy",    img: "/destinations/italy.jpg" },
    { c: "GR", n: "Greece",   img: "/destinations/greece.jpg" },
    { c: "JP", n: "Japan",    img: "/destinations/japan.jpg" },
    { c: "US", n: "USA",      img: "/destinations/usa.jpg" },
    { c: "BR", n: "Brazil",   img: "/destinations/brazil.jpg" },
    { c: "TH", n: "Thailand", img: "/destinations/thailand.jpg" },
  ];

  return (
    <Wrapper>
      <Header />  {/* ← reusable site banner */}

      <Hero>
        <HeroTitle>Where will you go next?</HeroTitle>
        <p style={{ fontSize: "1.15rem", maxWidth: 640 }}>
          Discover bundled getaways – flight, hotel &amp; experience in one click.
        </p>
      </Hero>

      <Grid>
        {dest.map(d => (
          <Card key={d.c} onClick={() => go(`/destinations/${d.c}`)}>
            <Img src={d.img} alt={d.n} />
            <Mask /><Label>{d.n}</Label>
          </Card>
        ))}
      </Grid>
    </Wrapper>
  );
}
