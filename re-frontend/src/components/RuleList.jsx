import { useEffect, useState } from 'react';
import apiClient from '../api/axios';
import { Link } from 'react-router-dom';

const RuleList = () => {
  const [rules, setRules] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [selectedRuleId, setSelectedRuleId] = useState(null);

  useEffect(() => {
    const fetchRules = async () => {
      try {
        const response = await apiClient.get('/');
        setRules(response.data);
      } catch (error) {
        console.error('Error fetching rules', error);
      }
    };
    fetchRules();
  }, []);

  const handleDelete = async (id) => {
    try {
      await apiClient.delete(`/${id}`);
      setRules(rules.filter((rule) => rule.id !== id));
    } catch (error) {
      console.error('Error deleting rule', error);
    } finally {
      setShowModal(false); // Hide the modal after the delete action
    }
  };

  const confirmDelete = (id) => {
    setSelectedRuleId(id);
    setShowModal(true);
  };

  const handleConfirmDelete = () => {
    if (selectedRuleId) {
      handleDelete(selectedRuleId);
    }
  };

  return (
    <div className='container'>
      <h2><strong>All Rules:</strong></h2>
      <ul className='rule-list'>
        {rules.map((rule) => (
          <li className='rule-list-item' key={rule.id}>
            {rule.ruleString} &nbsp;
            <div className="rule-actions">
              <button className='delete' onClick={() => confirmDelete(rule.id)}>Delete</button>
              <button className='evaluate'>
                <Link to={`/evaluate/${rule.id}`}>Evaluate</Link>
              </button>
            </div>
          </li>
        ))}
      </ul>

      {showModal && (
        <div className="modal show" style={{ display: 'block' }} tabIndex="-1" role="dialog">
          <div className="modal-dialog" role="document">
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title">Confirm Deletion</h5>
                <button type="button" className="close" onClick={() => setShowModal(false)}>
                  x
                </button>
              </div>
              <div className="modal-body">
                <p>Are you sure you want to delete this rule?</p>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
                <button type="button" className="btn btn-danger" onClick={handleConfirmDelete}>Yes, Delete</button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default RuleList;
