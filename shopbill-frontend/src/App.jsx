import React, { useEffect, useState } from "react";
import "./App.css";

const API_BASE_URL = "http://192.168.31.222:8080/api/billing";

const createEmptyItem = () => ({
  id: `${Date.now()}-${Math.random().toString(16).slice(2)}`,
  productName: "",
  price: "",
  quantity: 1,
});

export default function App() {
  const [customerName, setCustomerName] = useState("");
  const [mobileNumber, setMobileNumber] = useState("");
  const [customerEmail, setCustomerEmail] = useState("");
  const [customerAddress, setCustomerAddress] = useState("");
  const [items, setItems] = useState([createEmptyItem()]);
  const [isGeneratingPdf, setIsGeneratingPdf] = useState(false);
  const [isSendingEmail, setIsSendingEmail] = useState(false);
  const [isPreviewing, setIsPreviewing] = useState(false);
  const [previewUrl, setPreviewUrl] = useState("");
  const [toast, setToast] = useState(null);

  useEffect(() => {
    if (!toast) return undefined;
    const timer = window.setTimeout(() => setToast(null), 3500);
    return () => window.clearTimeout(timer);
  }, [toast]);

  useEffect(() => {
    return () => {
      if (previewUrl) URL.revokeObjectURL(previewUrl);
    };
  }, [previewUrl]);

  const showToast = (message, type = "success") => setToast({ message, type });

  const addItem = () => setItems((current) => [...current, createEmptyItem()]);

  const removeItem = (id) => {
    setItems((current) => current.filter((item) => item.id !== id));
  };

  const updateItem = (id, field, value) => {
    setItems((current) =>
      current.map((item) => (item.id === id ? { ...item, [field]: value } : item))
    );
  };

  const itemAmount = (item) =>
    (Number(item.price) || 0) * (Number(item.quantity) || 0);

  const grandTotal = items.reduce((sum, item) => sum + itemAmount(item), 0);

  const validateBill = () => {
    if (!customerName.trim()) {
      showToast("Please enter the customer name.", "error");
      return false;
    }
    if (!mobileNumber.trim()) {
      showToast("Please enter the customer mobile number.", "error");
      return false;
    }
    if (items.length === 0) {
      showToast("Please add at least one invoice item.", "error");
      return false;
    }

    for (let index = 0; index < items.length; index += 1) {
      const item = items[index];
      if (!item.productName.trim()) {
        showToast(`Please enter the name of item ${index + 1}.`, "error");
        return false;
      }
      if (
        item.price === "" ||
        !Number.isFinite(Number(item.price)) ||
        Number(item.price) < 0
      ) {
        showToast(`Please enter a valid price for item ${index + 1}.`, "error");
        return false;
      }
      if (
        item.quantity === "" ||
        !Number.isInteger(Number(item.quantity)) ||
        Number(item.quantity) < 1
      ) {
        showToast(`Please enter a valid quantity for item ${index + 1}.`, "error");
        return false;
      }
    }
    return true;
  };

  const buildRequest = () => ({
    customerName: customerName.trim(),
    mobileNumber: mobileNumber.trim(),
    customerEmail: customerEmail.trim(),
    customerAddress: customerAddress.trim(),
    items: items.map((item) => ({
      productName: item.productName.trim(),
      price: Number(item.price),
      quantity: Number(item.quantity),
    })),
  });

  const fetchPdf = async () => {
    const response = await fetch(`${API_BASE_URL}/pdf`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(buildRequest()),
    });

    if (!response.ok) {
      const message = await response.text();
      throw new Error(message || "Could not generate the PDF invoice.");
    }

    return response.blob();
  };

  const previewBill = async () => {
    if (!validateBill()) return;
    setIsPreviewing(true);
    try {
      const blob = await fetchPdf();
      const newUrl = URL.createObjectURL(blob);
      setPreviewUrl((oldUrl) => {
        if (oldUrl) URL.revokeObjectURL(oldUrl);
        return newUrl;
      });
      showToast("Bill preview is ready.");
    } catch (error) {
      console.error("PDF preview error:", error);
      showToast(error.message || "Could not preview the bill.", "error");
    } finally {
      setIsPreviewing(false);
    }
  };

  const generatePdf = async () => {
    if (!validateBill()) return;
    setIsGeneratingPdf(true);
    try {
      const blob = await fetchPdf();
      const url = URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = "mahesh-digital-studio-invoice.pdf";
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.setTimeout(() => URL.revokeObjectURL(url), 1000);
      showToast("PDF invoice downloaded successfully.");
    } catch (error) {
      console.error("PDF generation error:", error);
      showToast(error.message || "Failed to generate the PDF.", "error");
    } finally {
      setIsGeneratingPdf(false);
    }
  };

  const sendEmail = async () => {
    if (!validateBill()) return;
    if (!customerEmail.trim()) {
      showToast("Please enter the customer's email address.", "error");
      return;
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(customerEmail.trim())) {
      showToast("Please enter a valid email address.", "error");
      return;
    }

    setIsSendingEmail(true);
    try {
      const response = await fetch(`${API_BASE_URL}/email`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(buildRequest()),
      });
      const message = await response.text();
      if (!response.ok) throw new Error(message || "Failed to send the invoice email.");
      showToast(message || "Invoice emailed successfully.");
    } catch (error) {
      console.error("Email sending error:", error);
      showToast(error.message || "Failed to send the invoice email.", "error");
    } finally {
      setIsSendingEmail(false);
    }
  };

  const closePreview = () => {
    setPreviewUrl("");
  };

  const clearBill = () => {
    if (!window.confirm("Are you sure you want to clear this bill?")) return;
    setCustomerName("");
    setMobileNumber("");
    setCustomerEmail("");
    setCustomerAddress("");
    setItems([createEmptyItem()]);
    setToast(null);
    closePreview();
  };

  const busy = isGeneratingPdf || isSendingEmail || isPreviewing;

  return (
    <div className="app">
      {toast && (
        <div className={`toast toast-${toast.type}`} role="status" aria-live="polite">
          <span className="toast-icon" aria-hidden="true">
            {toast.type === "error" ? "!" : "✓"}
          </span>
          <span className="toast-message">{toast.message}</span>
          <button
            type="button"
            className="toast-close"
            onClick={() => setToast(null)}
            aria-label="Dismiss notification"
          >
            ×
          </button>
        </div>
      )}

      <header className="header">
        <div className="brand">
          <img
            src=".\src\assets\logo.png"
            alt="Mahesh Digital Studio logo"
            className="brand-logo"
          />
          <div>
            <h1>Mahesh Digital Studio</h1>
            <p>Smart Billing System</p>
          </div>
        </div>
        <div className="status">Billing Dashboard</div>
      </header>

      <main className="container">
        <section className="welcome">
          <span>SMART BILLING SYSTEM</span>
          <h2>Create a new invoice</h2>
          <p>Create professional invoices and send them directly to customers.</p>
        </section>

        <section className="panel">
          <h3>Customer Details</h3>
          <div className="form-grid">
            <div className="field">
              <label htmlFor="customerName">Customer Name</label>
              <input
                id="customerName"
                type="text"
                placeholder="Enter customer name"
                value={customerName}
                onChange={(event) => setCustomerName(event.target.value)}
              />
            </div>
            <div className="field">
              <label htmlFor="mobileNumber">Mobile Number</label>
              <input
                id="mobileNumber"
                type="tel"
                placeholder="Enter mobile number"
                value={mobileNumber}
                onChange={(event) => setMobileNumber(event.target.value)}
              />
            </div>
            <div className="field full-width">
              <label htmlFor="customerEmail">Email Address (Optional for PDF)</label>
              <input
                id="customerEmail"
                type="email"
                placeholder="Enter customer email"
                value={customerEmail}
                onChange={(event) => setCustomerEmail(event.target.value)}
              />
            </div>
            <div className="field full-width">
              <label htmlFor="customerAddress">Customer Address (Optional)</label>
              <textarea
                id="customerAddress"
                rows={2}
                placeholder="Enter customer's full address"
                value={customerAddress}
                onChange={(event) => setCustomerAddress(event.target.value)}
              />
            </div>
          </div>
        </section>

        <section className="panel">
          <div className="section-heading">
            <div>
              <h3>Invoice Items</h3>
              <p>Add products or services with their price and quantity.</p>
            </div>
            <button type="button" className="secondary-button" onClick={addItem}>
              + Add Item
            </button>
          </div>

          <div className="items-list">
            {items.map((item, index) => (
              <div className="item-row" key={item.id}>
                <div className="item-number">{index + 1}</div>

                <div className="field product-field">
                  <label htmlFor={`product-${item.id}`}>Product / Service</label>
                  <input
                    id={`product-${item.id}`}
                    type="text"
                    placeholder="Enter product or service"
                    value={item.productName}
                    onChange={(event) =>
                      updateItem(item.id, "productName", event.target.value)
                    }
                  />
                </div>

                <div className="field price-field">
                  <label htmlFor={`price-${item.id}`}>Price (₹)</label>
                  <input
                    id={`price-${item.id}`}
                    type="number"
                    min="0"
                    step="0.01"
                    placeholder="0.00"
                    value={item.price}
                    onChange={(event) => updateItem(item.id, "price", event.target.value)}
                  />
                </div>

                <div className="field quantity-field">
                  <label htmlFor={`quantity-${item.id}`}>Qty</label>
                  <input
                    id={`quantity-${item.id}`}
                    type="number"
                    min="1"
                    step="1"
                    value={item.quantity}
                    onChange={(event) =>
                      updateItem(item.id, "quantity", event.target.value)
                    }
                  />
                </div>

                <div className="item-amount">
                  <span>Amount</span>
                  <strong>
                    ₹{itemAmount(item).toLocaleString("en-IN", {
                      minimumFractionDigits: 2,
                      maximumFractionDigits: 2,
                    })}
                  </strong>
                </div>

                <button
                  type="button"
                  className="remove-button"
                  onClick={() => removeItem(item.id)}
                  aria-label={`Remove item ${index + 1}`}
                  title="Remove item"
                >
                  ×
                </button>
              </div>
            ))}
          </div>
        </section>

        <section className="total-panel">
          <span>Grand Total</span>
          <strong>
            ₹{grandTotal.toLocaleString("en-IN", {
              minimumFractionDigits: 2,
              maximumFractionDigits: 2,
            })}
          </strong>
        </section>

        <section className="actions">
          <button
            type="button"
            className="preview-button"
            onClick={previewBill}
            disabled={busy}
          >
            {isPreviewing ? "Preparing Preview..." : "Preview Bill"}
          </button>
          <button
            type="button"
            className="primary-button"
            onClick={generatePdf}
            disabled={busy}
          >
            {isGeneratingPdf ? "Generating PDF..." : "Generate PDF Invoice"}
          </button>
          <button
            type="button"
            className="primary-button"
            onClick={sendEmail}
            disabled={busy}
          >
            {isSendingEmail ? "Sending Email..." : "Send Invoice by Email"}
          </button>
          <button
            type="button"
            className="new-bill-button"
            onClick={clearBill}
            disabled={busy}
          >
            + New Bill
          </button>
        </section>

        <footer className="footer">
          <p>Mahesh Digital Studio · Thank you for your business!</p>
        </footer>
      </main>

      {previewUrl && (
        <div
          className="preview-overlay"
          role="presentation"
          onMouseDown={(event) => {
            if (event.target === event.currentTarget) closePreview();
          }}
        >
          <section
            className="preview-modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="preview-title"
          >
            <div className="preview-toolbar">
              <div>
                <h2 id="preview-title">Bill Preview</h2>
                <p>Review the PDF before downloading or emailing it.</p>
              </div>
              <div className="preview-toolbar-actions">
                <a
                  className="preview-download"
                  href={previewUrl}
                  download="mahesh-digital-studio-invoice.pdf"
                >
                  Download PDF
                </a>
                <button
                  type="button"
                  className="preview-close"
                  onClick={closePreview}
                  aria-label="Close bill preview"
                >
                  ×
                </button>
              </div>
            </div>
            <iframe
              className="preview-frame"
              src={previewUrl}
              title="Generated bill PDF preview"
            />
          </section>
        </div>
      )}
    </div>
  );
}
