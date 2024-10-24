
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import CreateRule from './pages/CreateRule';
import EvaluateRule from './pages/EvaluateRule';
import CombineRule from './pages/CombineRule';
import Footer from './components/Footer';
import Header from './components/Header';
import './App.css';

const App = () => {
  return (
  
     <Router>
      <Header/>
      
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/create" element={<CreateRule />} />
          <Route path="/evaluate/:id" element={<EvaluateRule />} />
          <Route path="/combine" element={<CombineRule/>} />
        </Routes>
   
      <Footer/>
    </Router>
    
  
   
   
  );
};

export default App;
