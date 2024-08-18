import { useState } from 'react';
import { api } from '../../utils/Api';
import CaveWindow from '../CaveWindow/CaveWindow';
import ControlPane from '../ControlPane/ControlPane';
import MazeWindow from '../MazeWindow/MazeWindow';
import './AppContainer.css';

function AppContainer() {
  const [maze, setMaze] = useState({});
  const [cave, setCave] = useState({});
  const [exitFlag, setExitFlag] = useState(false);

  const generateMaze = (event) => {
    event.preventDefault();
    const generateMazeForm = document.forms['mazeGenerate'];
    const rows = generateMazeForm.elements['rows'].value;
    const cols = generateMazeForm.elements['cols'].value;
    api.generateMaze(rows, cols)
    .then(data => {
      setCave(null);
      setMaze(data);
    })
  }

  const generateCave = (event) => {
    event.preventDefault();
    const generateCaveForm = document.forms['caveGenerate'];
    const rows = generateCaveForm.elements['rows'].value;
    const cols = generateCaveForm.elements['cols'].value;
    const probability = generateCaveForm.elements['probability'].value;
    api.generateCave(rows, cols, probability)
    .then(data => {
      setCave(data);
      setMaze(null);
    })
  }

  const getNewCaveGeneration = (event) => {
    event.preventDefault();
    const generateCaveForm = document.forms['caveNextGeneration'];
    const lifeLimit = generateCaveForm.elements['life limit'].value;
    const deathLimit = generateCaveForm.elements['death limit'].value;
    api.getNewCaveGeneration(cave, lifeLimit, deathLimit)
    .then(data =>{
      setCave(data);
    })
  }

  async function autoGenerateCave(event) {
    event.preventDefault();
    const autoGenerateForm = document.forms['autoGenerateCave'];
    const generateCaveForm = document.forms['caveNextGeneration'];
    const lifeLimit = generateCaveForm.elements['life limit'].value;
    const deathLimit = generateCaveForm.elements['death limit'].value;
    const delay = autoGenerateForm.elements['delay'].value;
    while (!exitFlag) {
      try {
        const newCave = await api.getNewCaveGeneration(cave, lifeLimit, deathLimit);
        if (equals(newCave, cave)) {
          setExitFlag(true);
        } else {
          setCave(newCave);
        }
        await sleep(delay);
      } catch (error) {
        console.error('Error generating new cave:', error);
        setExitFlag(true);
      }
    }
    setExitFlag(false);
  };

  const equals =(newCave, cave) =>{
    for(let i = 0; i < newCave.rows; i++){
      for(let j = 0; j < newCave.cols; j++){
        if(newCave.isAlive[i][j] !== cave.isAlive[i][j]){
          return false;
        }
      }
    }
    return true;
  }

  const  sleep = (ms) => {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  const stopAutoGenerating = (event) => {
    event.preventDefault();
    setExitFlag(true);
  }

  return (
    <section className='app-container'>
      {maze ? <MazeWindow
        maze={maze}
      /> : cave ? <CaveWindow
      cave={cave}
      ></CaveWindow> : <></>}
      <div className={"control-pane-container"}>
        <ControlPane
          header={"Generate new maze"}
          formName={"mazeGenerate"}
          fields={[
            {name: 'rows', min: 4, max:50, default: 20}, 
            {name: 'cols', min: 4, max:50, default: 20}
          ]}
          buttons={[
            {name: 'Generate', func:generateMaze}
          ]}

        />
        <ControlPane
          header={"Generate new cave"}
          formName={"caveGenerate"}
          fields={[
            {name: 'rows', min: 4, max:50, default: 40}, 
            {name: 'cols', min: 4, max:50, default: 40}, 
            {name: 'probability', min: 0, max:100, default: 50}
          ]}
          buttons={[
            {name: 'Generate', func:generateCave}
          ]}
        />
        <ControlPane
          header={"Get next generation"}
          formName={"caveNextGeneration"}
          fields={[
            {name: 'life limit', min: 0, max:7, default: 4}, 
            {name: 'death limit', min: 0, max:7, default: 4}
          ]}
          buttons={[
            {name: 'Get new generation', func:getNewCaveGeneration}
          ]}
        />
        <ControlPane
          header={"Get next generation"}
          formName={"autoGenerateCave"}
          fields={[
            {name: 'delay', min: 0, max:2000, default: 1000}
          ]}
          buttons={[
            {name: 'Auto Update', func:autoGenerateCave},
            {name: 'Stop', func:stopAutoGenerating}
          ]}
        />
      </div>
    </section>
  );
}

export default AppContainer;
