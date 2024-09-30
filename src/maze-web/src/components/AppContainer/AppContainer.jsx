import { Route, Routes } from 'react-router-dom';
import CavePage from '../../pages/CavePage';
import MazePage from '../../pages/MazePage';
import './AppContainer.css';

function AppContainer() {
  return (
    <Routes>
        <Route path='/'>
          <Route index element={<MazePage/>}/>
          <Route path='maze' element={<MazePage/>}/>
          <Route path='cave' element={<CavePage/>}/>
        </Route>
    </Routes>
  );
}

export default AppContainer;
