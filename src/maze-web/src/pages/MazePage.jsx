import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import ControlPane from '../components/ControlPane/ControlPane';
import MazeWindow from '../components/MazeWindow/MazeWindow';
import { api } from '../utils/Api';
import './Page.css';

function MazePage() {
  const [maze, setMaze] = useState(null);
  const [route, setRoute] = useState({});
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const [isAgentReady, setAgentReady] = useState(false);

  const generateMaze = (event) => {
    event.preventDefault();
    const generateMazeForm = document.forms['mazeGenerate'];
    const rows = generateMazeForm.elements['rows'].value;
    const cols = generateMazeForm.elements['cols'].value;
    if(rows > 50  || cols > 50 ){
      setError('Maximum size of the maze is 50x50');
    } else if(rows < 4  || cols < 4 ) {
      setError('Minimum size of the maze is 4x4');
    } else {
      api.generateMaze(rows, cols)
      .then(data => {
        setMaze(data);
        setError('');
        setAgentReady(false);
        setRoute({});
        setAgentReady(false);
      })
    }

  } 

  const moveTo = (event) => {
    event.preventDefault();
    navigate('/cave');
  }

  const parseMaze = (fileContent) => {
    const lines = fileContent.split('\n');
    const dimensions = lines[0].split(' ');
    const maze = {};
    maze.rows = parseInt(dimensions[0]);
    maze.cols = parseInt(dimensions[1]);
    maze.onRight = [];
    maze.onBottom = [];
    for(let i = 0; i < maze.rows; i++) {
      const lineOfRightWalls = lines[i + 1].split(' ');
      const lineOfBottomWalls = lines[i + 2 + maze.rows].split(' ');
      maze.onRight[i] = [];
      maze.onBottom[i] = [];
      for(let j = 0; j < maze.cols; j++){
        maze.onRight[i].push(parseInt(lineOfRightWalls[j]));
        maze.onBottom[i].push(parseInt(lineOfBottomWalls[j]));
      }
    }

    return maze;
  }

  const openMazeFile = (event) => {
    event.preventDefault();
    const fileInput = document.forms['control-pane-container__form'].elements['openMazeFile'];
    fileInput.click();
  };


  const handleMazeFileChange = (event) => {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        const content = e.target.result;
        try{
          const maze = parseMaze(content);
          setMaze(maze);
          setError('');
          setAgentReady(false);
        } catch (e){
          setError('Maze file format unsupported');
        }
        
      };
      reader.readAsText(file);
    }
  };

  const buildRoute = (event) => {
    event.preventDefault();
    const form = document.forms["routeBuild"];
    const fromX = form.elements['from X'].value;
    const fromY = form.elements['from Y'].value;
    const toX = form.elements['to X'].value;
    const toY = form.elements['to Y'].value;
    if(toX >= maze.cols || fromX >= maze.cols || toY >= maze.rows || fromY >= maze.rows || fromX < 0 || fromY < 0 || toX < 0 || toY < 0){
      setError('Invalid route params');
    } else {
      setError('');
      api.buildRoute(maze, fromX, fromY, toX, toY)
      .then(data => {
        setRoute(data);
      });
    }
  }

  const trainAgent = (event) => {
    event.preventDefault();
    const form = document.forms["agentTrain"];
    const toX = form.elements['to X'].value;
    const toY = form.elements['to Y'].value;
    if(toX >= maze.cols || toY >= maze.rows || toX < 0 || toY < 0 ){
      setError('Invalid route params');
    } else {
      setError('Agent training in PROCESS')
      api.trainAgent(maze, toX, toY)
      .then(() => {
        setError('Agent training is FINISHED');
        setAgentReady(true);
      });
    }
  }

  const agentSolve = (event) => {
    event.preventDefault();
    setError('');
    const form = document.forms["agentSolve"];
    const fromX = form.elements['from X'].value;
    const fromY = form.elements['from Y'].value;
    if(fromX >= maze.cols || fromY >= maze.rows || fromX < 0 || fromY < 0){
      setError('Invalid route params');
    } else {
      setError('');
      api.agentSolve(fromX, fromY)
      .then((data) => {
        setRoute(data);
      });
    }
  }


  return (
    <section className='page'>
      <div>
          <MazeWindow
            maze={maze}
            route={route}
          />
      </div>
      <div className={"control-pane-container"}>
        <form className={'control-pane-container__form'} name='control-pane-container__form'>
            <button className={'control-pane-container__button control-pane-container__button_active'} value={"maze"}>Mazes</button>
            <button className={'control-pane-container__button'} value={"cave"} onClick={moveTo}>Caves</button>
          <input className='control-pane-container__form-hidden-input' name='openMazeFile' type='file' onChange={handleMazeFileChange} id="input" multiple/>
            
        </form>
            <div>
              <ControlPane
                  header={"Load maze from file"}
                  formName={"mazeLoader"}
                  buttons={[
                    {name: 'Open file', func:openMazeFile},
                  ]}
              />
                <ControlPane
                  header={"Generate new maze"}
                  formName={"mazeGenerate"}
                  fields={[
                    {name: 'rows', min: 4, max:50, default: 50, step: 1}, 
                    {name: 'cols', min: 4, max:50, default: 50, step: 1}
                  ]}
                  buttons={[
                    {name: 'Generate', func:generateMaze},
                  ]}
              />
              {maze ? <div> <ControlPane
                header={"Build route"}
                formName={"routeBuild"}
                fields={[
                  {name: 'from X', min: 0, max:49, default: 0, step: 1}, 
                  {name: 'from Y', min: 0, max:49, default: 0, step: 1}, 
                  {name: 'to X', min: 0, max:49, default: 49, step: 1}, 
                  {name: 'to Y', min: 0, max:49, default: 49, step: 1}
                ]}
                buttons={[
                  {name: 'Build route', func:buildRoute}
                ]}
              /> 
              <ControlPane
                header={"QAgent training"}
                formName={"agentTrain"}
                fields={[
                  {name: 'to X', min: 0, max:49, default: 49, step: 1}, 
                  {name: 'to Y', min: 0, max:49, default: 49, step: 1}
                ]}
                buttons={[
                  {name: 'Train agent', func:trainAgent}
                ]}
              />
              {isAgentReady && <ControlPane
                header={"QAgent solve"}
                formName={"agentSolve"}
                fields={[
                  {name: 'from X', min: 0, max:49, default: 0, step: 1}, 
                  {name: 'from Y', min: 0, max:49, default: 0, step: 1}
                ]}
                buttons={[
                  {name: 'Solve', func:agentSolve}
                ]}
              />}
              </div>: <></>}
            </div>
        <div className={"error-message-container"}><p>{error}</p></div>
      </div>
    </section>
  );
}

export default MazePage;
