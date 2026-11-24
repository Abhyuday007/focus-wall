import React from "react";
import {AbsoluteFill, Img, interpolate, spring, staticFile, useCurrentFrame, useVideoConfig} from "remotion";
import {Video} from "@remotion/media";
import {TransitionSeries, linearTiming} from "@remotion/transitions";
import {fade} from "@remotion/transitions/fade";
import {slide} from "@remotion/transitions/slide";

const C = {bg: "#101419", panel: "#1D252D", green: "#03D26F", mint: "#9EF7C9", text: "#F3F7F6", muted: "#9CADB3", blue: "#8D9EBC"};
const font = "Inter, ui-sans-serif, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif";

const Grid = () => <AbsoluteFill style={{backgroundColor: C.bg, backgroundImage: "linear-gradient(rgba(255,255,255,.025) 1px, transparent 1px), linear-gradient(90deg, rgba(255,255,255,.025) 1px, transparent 1px)", backgroundSize: "54px 54px"}}/>;
const Glow = ({x, y, size = 560}: {x: number; y: number; size?: number}) => <div style={{position: "absolute", left: x, top: y, width: size, height: size, borderRadius: "50%", background: "radial-gradient(circle, rgba(3,210,111,.20), rgba(3,210,111,0) 68%)", filter: "blur(8px)"}}/>;

const Logo = ({size = 92}: {size?: number}) => <div style={{width: size, height: size, borderRadius: size * .28, background: "linear-gradient(145deg, #263039, #151B20)", border: "1px solid rgba(255,255,255,.12)", boxShadow: "0 18px 50px rgba(0,0,0,.35), inset 0 1px 0 rgba(255,255,255,.12)", display: "grid", placeItems: "center"}}>
  <svg viewBox="0 0 100 100" width={size * .7} height={size * .7}>
    <path d="M29 18h42M29 82h42M35 20c0 14 5 20 15 29-10 9-15 15-15 31M65 20c0 14-5 20-15 29 10 9 15 15 15 31" fill="none" stroke={C.green} strokeWidth="7" strokeLinecap="round"/>
    <path d="M39 65c7-6 15-6 22 0-2 9-6 14-11 14s-9-5-11-14Z" fill={C.mint}/>
    <path d="M43 36c-7-1-11 3-11 9 0 5 4 8 9 8M57 36c7-1 11 3 11 9 0 5-4 8-9 8M50 33v21" fill="none" stroke={C.text} strokeWidth="4" strokeLinecap="round"/>
  </svg>
</div>;

const Kicker = ({children}: {children: React.ReactNode}) => <div style={{fontFamily: font, fontSize: 28, fontWeight: 750, letterSpacing: 3, textTransform: "uppercase", color: C.green}}>{children}</div>;
const Enter = ({children, delay = 0, distance = 48}: {children: React.ReactNode; delay?: number; distance?: number}) => {
  const frame = useCurrentFrame(); const {fps} = useVideoConfig();
  const p = spring({frame: frame - delay, fps, config: {damping: 18, stiffness: 130, mass: .8}});
  return <div style={{opacity: p, transform: `translateY(${(1-p)*distance}px)`}}>{children}</div>;
};
const Shell = ({children}: {children: React.ReactNode}) => <AbsoluteFill style={{fontFamily: font, color: C.text, overflow: "hidden"}}><Grid/><Glow x={540} y={-190}/><div style={{position: "absolute", inset: "96px 82px 100px"}}>{children}</div></AbsoluteFill>;

const HookScene = () => {
  const frame = useCurrentFrame(); const blink = interpolate(frame % 40, [0,12,20,40], [1,1,.25,1]);
  return <Shell><Enter><Logo size={100}/></Enter><div style={{marginTop: 100}}><Enter delay={8}><Kicker>A familiar story</Kicker></Enter><Enter delay={16}><div style={{fontSize: 104, lineHeight: .98, fontWeight: 820, letterSpacing: -5, marginTop: 32, maxWidth: 890}}>You opened Instagram for <span style={{color: C.green}}>one message.</span></div></Enter><Enter delay={28}><div style={{fontSize: 48, lineHeight: 1.2, color: C.muted, marginTop: 58, maxWidth: 830}}>Then the infinite feed took over.<span style={{display: "inline-block", opacity: blink, color: C.green, marginLeft: 10}}>▮</span></div></Enter></div></Shell>;
};

const ClockScene = () => {
  const frame = useCurrentFrame(); const minutes = Math.min(20, Math.floor(interpolate(frame, [18,105], [0,20], {extrapolateLeft: "clamp", extrapolateRight: "clamp"})));
  return <Shell><Enter><Kicker>The real cost</Kicker></Enter><div style={{display: "flex", alignItems: "center", justifyContent: "space-between", height: 1050}}><div style={{width: 550}}><Enter delay={8}><div style={{fontSize: 230, fontWeight: 850, letterSpacing: -14, lineHeight: .9, color: C.green}}>{minutes}</div></Enter><Enter delay={12}><div style={{fontSize: 92, fontWeight: 820, letterSpacing: -4}}>minutes later.</div></Enter><Enter delay={28}><div style={{fontSize: 45, lineHeight: 1.28, color: C.muted, marginTop: 46}}>Still scrolling.<br/>Still not connected.</div></Enter></div><Enter delay={20} distance={80}><div style={{width: 310, height: 650, borderRadius: 60, background: C.panel, border: "2px solid rgba(255,255,255,.14)", padding: 18, boxShadow: "0 40px 90px #0008", transform: "rotate(5deg)"}}><div style={{height: "100%", borderRadius: 43, background: "linear-gradient(#28343E,#11161B)", overflow: "hidden", position: "relative"}}>{[0,1,2,3].map(n => <div key={n} style={{height: 145, margin: 12, borderRadius: 25, background: n%2 ? "#34434E" : "#222D35", transform: `translateY(${-((frame*4)%157)}px)`}}/>)}<div style={{position: "absolute", inset: 0, background: "linear-gradient(transparent 45%, rgba(3,210,111,.15))"}}/></div></div></Enter></div></Shell>;
};

const ReframeScene = () => <Shell><Enter><Kicker>So I built Focus Wall</Kicker></Enter><div style={{marginTop: 62}}><Enter delay={8}><div style={{fontSize: 94, lineHeight: 1.02, fontWeight: 820, letterSpacing: -4}}>Keep the <span style={{color: C.green}}>people.</span><br/>Remove the trap.</div></Enter><Enter delay={24}><div style={{display: "grid", gridTemplateColumns: "1fr 1fr", gap: 24, marginTop: 65}}>{["Stories","Messages","Notifications","Posting"].map(item => <div key={item} style={{padding: "29px 32px", borderRadius: 30, background: C.panel, border: "1px solid rgba(255,255,255,.09)", display: "flex", alignItems: "center", gap: 22}}><div style={{width: 48, height: 48, borderRadius: 15, background: "rgba(3,210,111,.14)", color: C.green, display: "grid", placeItems: "center", fontSize: 28, fontWeight: 900}}>✓</div><div style={{fontSize: 40, fontWeight: 700}}>{item}</div></div>)}</div></Enter><Enter delay={42}><div style={{fontSize: 36, color: C.muted, marginTop: 42, lineHeight: 1.35}}>Social media stays useful.<br/><span style={{color: C.text}}>Infinite Reels and Shorts don’t.</span></div></Enter></div></Shell>;

const Phone = ({children, rotate = 0}: {children: React.ReactNode; rotate?: number}) => <div style={{width: 430, height: 930, borderRadius: 62, background: "#050708", border: "3px solid #53616A", padding: 14, boxShadow: "0 50px 110px #000A", transform: `rotate(${rotate}deg)`, overflow: "hidden"}}><div style={{height: "100%", borderRadius: 47, overflow: "hidden", background: C.panel, position: "relative"}}>{children}<div style={{position: "absolute", top: 10, left: "50%", transform: "translateX(-50%)", width: 120, height: 28, borderRadius: 30, background: "#050708"}}/></div></div>;

const ProofScene = () => {
  const frame = useCurrentFrame(); const scale = interpolate(frame, [0,22], [.92,1], {extrapolateRight: "clamp"});
  return <Shell><Enter><Kicker>Real control, on your phone</Kicker></Enter><div style={{display: "flex", gap: 62, alignItems: "center", marginTop: 44}}><div style={{transform: `scale(${scale})`}}><Phone rotate={-2}><Video src={staticFile("media/focus-wall-guide.mp4")} muted loop objectFit="cover" style={{width: "100%", height: "100%"}}/></Phone></div><div style={{flex: 1}}><Enter delay={16}><div style={{fontSize: 72, lineHeight: 1.03, fontWeight: 820, letterSpacing: -3}}>Choose your daily limit.</div></Enter><Enter delay={29}><div style={{fontSize: 39, lineHeight: 1.4, color: C.muted, marginTop: 42}}>The timer runs only while you’re actually watching Reels or Shorts.</div></Enter><Enter delay={43}><div style={{marginTop: 52, padding: "30px 32px", borderRadius: 28, background: "rgba(3,210,111,.10)", border: "1px solid rgba(3,210,111,.34)"}}><div style={{fontSize: 30, color: C.mint, fontWeight: 760}}>0–60 minutes · resets daily</div></div></Enter></div></div></Shell>;
};

const BoundaryScene = () => <Shell><Enter><Kicker>When the budget ends</Kicker></Enter><Enter delay={8}><div style={{fontSize: 83, lineHeight: 1.03, fontWeight: 820, letterSpacing: -4, marginTop: 38}}>The feed stops.<br/><span style={{color: C.green}}>Your connections don’t.</span></div></Enter><div style={{display: "flex", gap: 38, marginTop: 58, alignItems: "center"}}><Enter delay={20}><Phone rotate={-3}><Img src={staticFile("media/focus-wall-v150-controls.jpg")} style={{width: "100%", height: "100%", objectFit: "cover"}}/></Phone></Enter><Enter delay={32}><div style={{width: 410}}>{["Shared Reel: one view","Stories & DMs: available","Infinite feed: blocked","YouTube Home: available"].map((x,i) => <div key={x} style={{fontSize: 34, lineHeight: 1.2, fontWeight: 650, marginBottom: 23, padding: "24px 26px", borderRadius: 24, background: i===2 ? "rgba(3,210,111,.12)" : C.panel, border: `1px solid ${i===2 ? "rgba(3,210,111,.36)" : "rgba(255,255,255,.08)"}`}}>{i===2 ? "◆ " : "✓ "}{x}</div>)}</div></Enter></div></Shell>;

const TrustScene = () => <Shell><Enter><Kicker>Built to respect you</Kicker></Enter><Enter delay={10}><div style={{fontSize: 90, lineHeight: 1.03, fontWeight: 820, letterSpacing: -4, marginTop: 42}}>No ads.<br/>No Instagram login.<br/><span style={{color: C.green}}>No cloud tracking.</span></div></Enter><Enter delay={30}><div style={{marginTop: 72, display: "flex", flexWrap: "wrap", gap: 18}}>{["Local-first","Open source","Android","Accessibility","API 36","CI tested"].map(x => <div key={x} style={{fontSize: 30, fontWeight: 700, color: C.mint, background: C.panel, border: "1px solid rgba(255,255,255,.09)", borderRadius: 999, padding: "18px 26px"}}>{x}</div>)}</div></Enter><Enter delay={48}><div style={{fontSize: 38, color: C.muted, lineHeight: 1.35, marginTop: 66}}>Built as a student project.<br/>Designed as a real product.</div></Enter></Shell>;

const CTAScene = () => {
  const frame = useCurrentFrame(); const {fps} = useVideoConfig(); const p = spring({frame, fps, config: {damping: 16, stiffness: 95}}); const pulse = interpolate(Math.sin(frame/9), [-1,1], [.94,1.04]);
  return <Shell><div style={{display: "flex", flexDirection: "column", alignItems: "center", textAlign: "center", paddingTop: 25}}><div style={{transform: `scale(${p})`}}><Logo size={135}/></div><Enter delay={12}><div style={{fontSize: 104, fontWeight: 850, letterSpacing: -6, marginTop: 42}}>Focus Wall</div></Enter><Enter delay={23}><div style={{fontSize: 43, lineHeight: 1.3, color: C.muted, maxWidth: 820, marginTop: 18}}>Stay connected.<br/><span style={{color: C.text}}>Scroll with intention.</span></div></Enter><Enter delay={38}><div style={{transform: `scale(${pulse})`, marginTop: 58, padding: "27px 40px", borderRadius: 26, background: C.green, color: "#07110C", fontSize: 31, fontWeight: 850, boxShadow: "0 20px 70px rgba(3,210,111,.22)"}}>View the project on GitHub →</div></Enter><Enter delay={48}><div style={{fontFamily: "ui-monospace, SFMono-Regular, Menlo, monospace", fontSize: 29, color: C.mint, marginTop: 32}}>github.com/Abhyuday007/focus-wall</div></Enter><Enter delay={60}><div style={{fontSize: 25, color: C.blue, marginTop: 27}}>Open source · Android · Local-first</div></Enter></div></Shell>;
};

export const FocusWallPromo: React.FC = () => <AbsoluteFill style={{backgroundColor: C.bg}}><TransitionSeries>
  <TransitionSeries.Sequence durationInFrames={150}><HookScene/></TransitionSeries.Sequence><TransitionSeries.Transition presentation={slide({direction: "from-right"})} timing={linearTiming({durationInFrames: 15})}/>
  <TransitionSeries.Sequence durationInFrames={150}><ClockScene/></TransitionSeries.Sequence><TransitionSeries.Transition presentation={fade()} timing={linearTiming({durationInFrames: 15})}/>
  <TransitionSeries.Sequence durationInFrames={150}><ReframeScene/></TransitionSeries.Sequence><TransitionSeries.Transition presentation={slide({direction: "from-right"})} timing={linearTiming({durationInFrames: 15})}/>
  <TransitionSeries.Sequence durationInFrames={210}><ProofScene/></TransitionSeries.Sequence><TransitionSeries.Transition presentation={fade()} timing={linearTiming({durationInFrames: 15})}/>
  <TransitionSeries.Sequence durationInFrames={180}><BoundaryScene/></TransitionSeries.Sequence><TransitionSeries.Transition presentation={slide({direction: "from-right"})} timing={linearTiming({durationInFrames: 15})}/>
  <TransitionSeries.Sequence durationInFrames={150}><TrustScene/></TransitionSeries.Sequence><TransitionSeries.Transition presentation={fade()} timing={linearTiming({durationInFrames: 15})}/>
  <TransitionSeries.Sequence durationInFrames={180}><CTAScene/></TransitionSeries.Sequence>
</TransitionSeries></AbsoluteFill>;
