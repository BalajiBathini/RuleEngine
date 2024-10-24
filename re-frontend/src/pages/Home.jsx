import { useState } from 'react';
import RuleForm from '../components/RuleForm';
import RuleList from '../components/RuleList';

const Home = () => {
  const [rules, setRules] = useState([]);

  const handleSuccess = (newRule) => {
    setRules([...rules, newRule]); // Add the new rule to the list of rules
  };

  return (
    <div>
      <h1>Rule Engine With AST</h1>
      <RuleForm onSuccess={handleSuccess} /> {/* Pass the onSuccess prop */}
      <RuleList rules={rules} />
    </div>
  );
};

export default Home;
