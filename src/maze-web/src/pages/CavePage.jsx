import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import CaveWindow from '../components/CaveWindow/CaveWindow';
import ControlPane from '../components/ControlPane/ControlPane';
import { api } from '../utils/Api';

function CavePage() {
  const [cave, setCave] = useState(null);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const generateCave = (event) => {
    event.preventDefault();
    const generateCaveForm = document.forms['caveGenerate'];
    const rows = generateCaveForm.elements['rows'].value;
    const cols = generateCaveForm.elements['cols'].value;
    const probability = generateCaveForm.elements['probability'].value;
    api.generateCave(rows, cols, probability)
    .then(data => {
      setCave(data);
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
    let currentCave = cave;
    let innerExitFlag = false;
    let counter = 0;
    while (!innerExitFlag && counter < 40) {
      counter++;
      try {
        const newCave = await api.getNewCaveGeneration(currentCave, lifeLimit, deathLimit);
        if (equals(newCave, currentCave)) {
          innerExitFlag = true;
        } else {
          setCave(newCave);
          currentCave = newCave;
        }
        await sleep(delay);
      } catch (error) {
        console.error('Error generating new cave:', error);
        innerExitFlag = true;
      }
    }
  }
  

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

  const moveTo = (event) => {
    event.preventDefault();
    navigate('/maze');
  
  }



  const parseCave = (fileContent) => {
    const lines = fileContent.split('\n');
    const dimensions = lines[0].split(' ');
    if (dimensions.length < 2) {
      throw new Error('Invalid file format: missing dimensions');
    }
  
    const rows = parseInt(dimensions[0]);
    const cols = parseInt(dimensions[1]);
  
    if (isNaN(rows) || isNaN(cols) || rows <= 0 || cols <= 0) {
      throw new Error('Invalid file format: dimensions must be positive integers');
    }
  
    const cave = { rows, cols, isAlive: [] };
    for(let i = 0; i < cave.rows; i++) {
      const lineOfAliveCells = lines[i + 1].split(' ');
      cave.isAlive[i] = [];
      for(let j = 0; j < cave.cols; j++){
        cave.isAlive[i].push(parseInt(lineOfAliveCells[j]));
      }
    }
    return cave;
  }

  const openCaveFile = (event) => {
    event.preventDefault();
    const fileInput = document.forms['control-pane-container__form'].elements['openCaveFile'];
    fileInput.click();
  }

  const handleCaveFileChange = (event) => {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        const content = e.target.result;
        try{
          const cave = parseCave(content);
          setCave(cave);
          setError('');
        } catch (e){
          setError('Cave file format unsupported');
        }
        
      };
      reader.readAsText(file);
    }
  };

  return (
    <section className='page'>
      <div>
        <CaveWindow
            cave={cave}
          />
      </div>
      <div className={"control-pane-container"}>
        <form className={'control-pane-container__form'} name='control-pane-container__form'>
            <button className={'control-pane-container__button'} value={"maze"} onClick={moveTo}>Mazes</button>
            <button className={'control-pane-container__button control-pane-container__button_active'} value={"cave"}>Caves</button>
            <input className='control-pane-container__form-hidden-input' name='openCaveFile' type='file' onChange={handleCaveFileChange} id="input" multiple/>
        </form>
            <div>
              <ControlPane
                  header={"Load cave from file"}
                  formName={"caveLoader"}
                  buttons={[
                    {name: 'Open file', func:openCaveFile},
                  ]}
              />
              <ControlPane
                header={"Generate new cave"}
                formName={"caveGenerate"}
                fields={[
                  {name: 'rows', min: 4, max:50, default: 50, step: 1}, 
                  {name: 'cols', min: 4, max:50, default: 50, step: 1}, 
                  {name: 'probability', min: 0, max:100, default: 50, step: 1}
                ]}
                buttons={[
                  {name: 'Generate', func:generateCave}
                ]}
              />
             {cave ? 
             <div>
             <ControlPane
                header={"Get next generation"}
                formName={"caveNextGeneration"}
                fields={[
                  {name: 'life limit', min: 0, max:7, default: 4, step: 1}, 
                  {name: 'death limit', min: 0, max:7, default: 4, step: 1}
                ]}
                buttons={[
                  {name: 'Get new generation', func:getNewCaveGeneration}
                ]}
              />
              <ControlPane
                header={"Auto generation"}
                formName={"autoGenerateCave"}
                fields={[
                  {name: 'delay', min: 0, max:2000, default: 100, step: 100}
                ]}
                buttons={[
                  {name: 'Auto Update', func:autoGenerateCave}
                ]}
              /> 
              
              </div>
              :
              <></>}
              
            </div>

        <div className={"error-message-container"}><p>{error}</p></div>
      </div>
    </section>
  );
}

export default CavePage;
