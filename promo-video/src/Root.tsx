import {Composition} from "remotion";
import {FocusWallPromo} from "./Composition";

export const RemotionRoot: React.FC = () => <Composition id="FocusWallLinkedIn" component={FocusWallPromo} durationInFrames={1080} fps={30} width={1080} height={1350}/>;
