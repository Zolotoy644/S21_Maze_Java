import { BrowserRouter } from 'react-router-dom';
import '../../vendor/normalize.css';
import AppContainer from '../AppContainer/AppContainer';
import './App.css';

function App() {
  
  return (
    <BrowserRouter>
      <div className="app">
        <AppContainer/>
      </div>
    </BrowserRouter>
  );
}

export default App;
