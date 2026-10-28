const $ = (id) => document.getElementById(id);
let currentOrderCode = "";
let toastTimer;
function toast(message){const el=$("toast");el.textContent=message;el.classList.add("show");clearTimeout(toastTimer);toastTimer=setTimeout(()=>el.classList.remove("show"),3200);}
function showResult(targetId, title, status, data, error=false){
  const target=$(targetId); target.classList.remove("empty");
  const safe = JSON.stringify(data, null, 2);
  target.innerHTML = `<div class="result-head"><span>${escapeHtml(title)}</span><span class="${error?'err':'ok'}">${escapeHtml(String(status))}</span></div><pre></pre>`;
  target.querySelector("pre").textContent = safe;
}
function escapeHtml(s){return String(s).replace(/[&<>"']/g,c=>({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[c]));}
async function api(path, method="GET", body=null){
  const options={method,headers:{"Accept":"application/json"}};
  if(body!==null){options.headers["Content-Type"]="application/json";options.body=JSON.stringify(body);}
  const response=await fetch(path,options);
  const text=await response.text();
  let data=text;
  try{data=text?JSON.parse(text):{message:"Request completed with no response body"};}catch{}
  if(!response.ok){const msg=typeof data==="string"?data:(data.message||data.error||JSON.stringify(data));throw Object.assign(new Error(msg),{status:response.status,data});}
  return {status:response.status,data};
}
async function action(targetId,title,fn){
  try{const result=await fn();showResult(targetId,title,`${result.status} · success`,result.data);toast(`${title} completed`);return result.data;}
  catch(e){showResult(targetId,title,`${e.status||"ERROR"} · failed`,e.data||{message:e.message},true);toast(`${title} failed: ${e.message}`);return null;}
}
function value(id){return $(id).value.trim();}
function num(id){return Number(value(id));}
function form(id,fn){$(id).addEventListener("submit",async e=>{e.preventDefault();await fn();});}
$("checkHealth").addEventListener("click",async()=>{
  $("apiStatus").textContent="Checking…";
  try{
    // There is no dedicated health endpoint assumed here; use a harmless missing-resource-free check only after a code is supplied.
    const code=value("orderCode");
    if(!code){$("apiStatus").textContent="Ready to test";$("apiStatusDetail").textContent="Enter an order code and use Find order";$("statusDot").className="status-dot idle";toast("No health endpoint is defined; try an order lookup.");return;}
    await api(`/api/orders/${encodeURIComponent(code)}`);
    $("apiStatus").textContent="API reachable";$("apiStatusDetail").textContent="Order lookup succeeded";$("statusDot").className="status-dot good";
  }catch(e){$("apiStatus").textContent=e.status===404?"Server responded":"Request failed";$("apiStatusDetail").textContent=e.status?`HTTP ${e.status} from order lookup`:"Could not reach the API";$("statusDot").className=`status-dot ${e.status?"good":"bad"}`;}
});
form("orderLookupForm",async()=>{
  currentOrderCode=value("orderCode");
  const data=await action("orderResult","Order lookup",()=>api(`/api/orders/${encodeURIComponent(currentOrderCode)}`));
  $("loadOrderLines").disabled=!data;
  if(data){$("apiStatus").textContent="API reachable";$("apiStatusDetail").textContent="Order lookup succeeded";$("statusDot").className="status-dot good";}
});
$("loadOrderLines").addEventListener("click",()=>action("orderLinesResult","Order lines",()=>api(`/api/orders/${encodeURIComponent(currentOrderCode)}/lines`)));
form("createReturnForm",()=>action("returnResult","Create return request",()=>api("/api/returns","POST",{returnCode:value("newReturnCode"),orderCode:value("newReturnOrderCode"),reason:value("newReturnReason")})));
form("getReturnForm",()=>action("returnResult","Return request lookup",()=>api(`/api/returns/${encodeURIComponent(value("lookupReturnCode"))}`)));
form("addReturnLineForm",()=>action("returnLinesResult","Add return line",()=>api(`/api/returns/${encodeURIComponent(value("lineReturnCode"))}/lines`,"POST",{orderLineId:num("orderLineId"),quantityRequested:num("quantityRequested")})));
form("getReturnLinesForm",()=>action("returnLinesResult","Return lines",()=>api(`/api/returns/${encodeURIComponent(value("listLinesReturnCode"))}/lines`)));
form("decisionForm",()=>{
  let body;
  try{body=JSON.parse(value("decisionBody")||"{}");}catch{toast("Decision request JSON is invalid");return;}
  const code=encodeURIComponent(value("decisionReturnCode")), decision=value("decisionType");
  return action("returnResult",`Return ${decision}`,()=>api(`/api/returns/${code}/${decision}`,"POST",body));
});
form("markInspectedForm",()=>action("returnResult","Mark return inspected",()=>api(`/api/returns/${encodeURIComponent(value("markInspectedCode"))}/inspected`,"POST")));
form("inspectionForm",()=>action("inspectionResult","Record inspection",()=>api("/api/inspections","POST",{returnLineId:num("inspectionReturnLineId"),conditionFound:value("conditionFound"),notes:value("inspectionNotes")||null})));
form("getInspectionForm",()=>action("inspectionResult","Inspection lookup",()=>api(`/api/inspections/return-line/${num("getInspectionLineId")}`)));
form("dispositionForm",()=>action("dispositionResult","Create disposition",()=>api("/api/inventory/dispositions","POST",{inspectionId:num("dispositionInspectionId"),dispositionType:value("dispositionType"),decisionReason:value("dispositionReason")})));
form("getDispositionForm",()=>action("dispositionResult","Disposition lookup",()=>api(`/api/inventory/dispositions/inspection/${num("getDispositionInspectionId")}`)));
form("createShipmentForm",()=>action("shipmentResult","Create return shipment",()=>api("/api/return-shipments","POST",{shipmentCode:value("shipmentCode"),returnCode:value("shipmentReturnCode"),carrier:value("shipmentCarrier")||null,trackingNumber:value("trackingNumber")||null})));
form("shipmentStatusForm",()=>{const code=encodeURIComponent(value("statusShipmentCode"));return action("shipmentResult","Update shipment",()=>api(`/api/return-shipments/${code}/${value("shipmentAction")}`,"POST"));});
form("getShipmentsForm",()=>action("shipmentResult","Return shipments",()=>api(`/api/return-shipments/return/${encodeURIComponent(value("shipmentsReturnCode"))}`)));
form("createRefundForm",()=>action("refundResult","Create refund",()=>api("/api/refunds","POST",{returnRequestId:num("refundReturnRequestId")})));
form("getRefundForm",()=>action("refundResult","Refund lookup",()=>api(`/api/refunds/${num("refundId")}`)));
document.querySelectorAll(".nav-item").forEach(a=>a.addEventListener("click",()=>{document.querySelectorAll(".nav-item").forEach(x=>x.classList.remove("active"));a.classList.add("active");}));
